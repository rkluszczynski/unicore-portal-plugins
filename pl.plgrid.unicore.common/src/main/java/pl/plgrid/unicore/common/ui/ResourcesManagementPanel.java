package pl.plgrid.unicore.common.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.ui.PortalApplication;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;
import pl.plgrid.unicore.common.model.BrokerJobModel;
import pl.plgrid.unicore.common.ui.model.ResourceSetComponent;

import java.util.Set;

/**
 * @author Rafal
 */
public class ResourcesManagementPanel extends CustomComponent {
    private static final Logger logger = Logger.getLogger(ResourcesManagementPanel.class);

    private StringTokensPanel stringTokensPanel = new StringTokensPanel();

    public <T extends Component & ResourceSetComponent> ResourcesManagementPanel(
            final BrokerJobModel brokerJobModel,
            T onTopComponent,
            Set<String> excludeResourceNames
    ) {
        createComponents(brokerJobModel, onTopComponent, excludeResourceNames);
    }

    private <T extends Component & ResourceSetComponent> void createComponents(
            final BrokerJobModel brokerJobModel,
            T onTopComponent,
            Set<String> excludeResourceNames
    ) {
        final Set<String> excludeResources = excludeResourceNames;
        if (onTopComponent != null) {
            excludeResources.addAll(onTopComponent.getResources().keySet());
        }

        Button showAvailableResourcesWindowButton = new Button(getMessage("showWindow"));
        showAvailableResourcesWindowButton.setStyleName(Reindeer.BUTTON_SMALL);
        showAvailableResourcesWindowButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Window w = new Window(getMessage("windowTitle"));
                AvailableResourcesWindowPanel resourcesPanel = new AvailableResourcesWindowPanel(
                        w,
                        brokerJobModel,
                        stringTokensPanel,
                        excludeResources
                );
                w.setContent(resourcesPanel);
                w.setHeight(AvailableResourcesWindowPanel.INITIAL_HEIGHT, Unit.PIXELS);
                w.setWidth(AvailableResourcesWindowPanel.INITIAL_WIDTH, Unit.PIXELS);
                w.setModal(true);
                w.center();

                PortalApplication.getCurrent().addWindow(w);
            }
        });

        int componentRowNumber = -1;
        GridLayout gridLayout = new GridLayout(1, onTopComponent == null ? 2 : 3);

        if (onTopComponent != null) {
            ++componentRowNumber;
            logger.info("ON TOP COMPONENT !!!!!!!!!!!!!!!!!!!!!! " + onTopComponent.getClass().getCanonicalName());
            gridLayout.addComponent(onTopComponent, 0, componentRowNumber);
            gridLayout.setComponentAlignment(onTopComponent, Alignment.MIDDLE_LEFT);
            gridLayout.setRowExpandRatio(componentRowNumber, 1.f);
        }

        ++componentRowNumber;
        gridLayout.addComponent(showAvailableResourcesWindowButton, 0, componentRowNumber);
        gridLayout.setComponentAlignment(showAvailableResourcesWindowButton, Alignment.MIDDLE_CENTER);

        ++componentRowNumber;
        gridLayout.addComponent(stringTokensPanel, 0, componentRowNumber);
        gridLayout.setComponentAlignment(stringTokensPanel, Alignment.TOP_LEFT);
        gridLayout.setRowExpandRatio(componentRowNumber, 1.f);

        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.setSizeFull();

        setCompositionRoot(gridLayout);
        setSizeFull();
    }


    private String getMessage(String messageKey) {
        return GlobalState.getMessage(CommonComponentsI18N.ID, "resourcesPanel." + messageKey);
    }
}
