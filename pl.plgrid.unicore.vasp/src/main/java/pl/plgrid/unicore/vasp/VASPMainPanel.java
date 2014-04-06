package pl.plgrid.unicore.vasp;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.ui.Styles;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.model.BrokerJobModel;
import pl.plgrid.unicore.common.ui.JobsTableViewer;
import pl.plgrid.unicore.vasp.i18n.VASPViewI18N;
import pl.plgrid.unicore.vasp.input.SubmissionPanel;

/**
 * @author rkluszczynski
 */
public class VASPMainPanel extends VerticalLayout {
    private static final Logger logger = Logger.getLogger(VASPMainPanel.class);

    private static String APPLICATION_NAME = "VASP";
    private static String APPLICATION_VERSION = "5.2";


    public VASPMainPanel() {
        super();

        logger.info("Creating VASP view for user: " + Session.getCurrent().getUser().getUsername());
        createMainViewComponents(
                new BrokerJobModel(APPLICATION_NAME, APPLICATION_VERSION)
        );
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible == true) {
            super.setVisible(true);
        } else {
            super.setVisible(false);
        }
    }

    private void createMainViewComponents(BrokerJobModel brokerJobModel) {
        VerticalSplitPanel splitPanel = new VerticalSplitPanel(
                new JobsTableViewer(),
                new SubmissionPanel(brokerJobModel)
        );
//        splitPanel.setCaption(getMessage("title"));
        splitPanel.setSplitPosition(20, Unit.PERCENTAGE);
        splitPanel.setMinSplitPosition(10, Unit.PERCENTAGE);
        splitPanel.setMaxSplitPosition(50, Unit.PERCENTAGE);

        addComponent(splitPanel);
        addStyleName(Styles.PADDING_All_10);
        setSizeFull();
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(VASPViewI18N.ID, "vasp.main." + messageKey);
    }
}
