package pl.plgrid.unicore.vasp;

import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.authn.UserMetadataAttribute;
import eu.unicore.portal.ui.Styles;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.model.BrokerJobModel;
import pl.plgrid.unicore.common.resources.StandardResources;
import pl.plgrid.unicore.common.ui.SimulationsTableViewer;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;
import pl.plgrid.unicore.vasp.input.SubmissionPanel;
import pl.plgrid.unicore.vasp.input.VASPProperties;

import java.util.List;
import java.util.Set;

import static pl.plgrid.unicore.vasp.input.SubmissionPanel.VASP_SIMULATION_DEFAULT_PREFIX;

/**
 * @author rkluszczynski
 */
class VASPMainPanel extends VerticalLayout {

    public VASPMainPanel() {
        super();
        logger.info("Creating VASP view for user: " + Session.getCurrent().getUser().getUsername());

        UserMetadataAttribute attributes = SecurityHelper.getUserAttributes();
        List<String> accessAttribute = attributes.getUserAttribute(ACCESS_ATTRIBUTE_KEY);
        if (accessAttribute == null || accessAttribute.isEmpty()
                || !accessAttribute.contains(ACCESS_ATTRIBUTE_VALUE)) {
            Notification.show("VASP", "You do not have permissions to use VASP", Notification.Type.WARNING_MESSAGE);
        }

        createMainViewComponents(
                new BrokerJobModel(APPLICATION_NAME, APPLICATION_VERSION)
        );
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            super.setVisible(true);
        } else {
            super.setVisible(false);
        }
    }

    private void createMainViewComponents(BrokerJobModel brokerJobModel) {
        Set<String> excludeResourceNames = Sets.<String>newHashSet(
                StandardResources.cpuArchitecture.name(),
                StandardResources.osType.name()
        );

        VerticalLayout simulationsTablePanel = new VerticalLayout(
                new SimulationsTableViewer(VASP_SIMULATION_DEFAULT_PREFIX)
        );
        simulationsTablePanel.setMargin(new MarginInfo(false, false, true, false));
        simulationsTablePanel.setSizeFull();

        VerticalLayout submissionPanel = new VerticalLayout(
                new SubmissionPanel(brokerJobModel, excludeResourceNames,
                        new VASPProperties(GlobalState.getCurrent().getPortalConfiguration().getProperties())
                ));
        submissionPanel.setMargin(new MarginInfo(true, false, false, false));
        submissionPanel.setSizeFull();

        VerticalSplitPanel splitPanel = new VerticalSplitPanel(
                simulationsTablePanel, submissionPanel
        );
//        splitPanel.setCaption(getMessage("title"));
        splitPanel.setSplitPosition(20, Unit.PERCENTAGE);
        splitPanel.setMinSplitPosition(10, Unit.PERCENTAGE);
        splitPanel.setMaxSplitPosition(50, Unit.PERCENTAGE);

        addComponent(splitPanel);
        addStyleName(Styles.PADDING_All_10);
        setSizeFull();
    }

    private static final String APPLICATION_NAME = "VASP";
    private static final String APPLICATION_VERSION = "5.2";

    private static final String ACCESS_ATTRIBUTE_KEY = "memberOf";
    private static final String ACCESS_ATTRIBUTE_VALUE = "/vo.plgrid.pl/groups/plggvasp5";

    private static final Logger logger = Logger.getLogger(VASPMainPanel.class);
}
