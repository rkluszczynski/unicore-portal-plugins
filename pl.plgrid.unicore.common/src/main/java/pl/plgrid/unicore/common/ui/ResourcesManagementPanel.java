package pl.plgrid.unicore.common.ui;

import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.ui.PortalApplication;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;
import pl.plgrid.unicore.common.model.BrokerJobModel;

import java.util.Set;

/**
 * @author Rafal
 */
public class ResourcesManagementPanel extends CustomComponent {

    private StringTokensPanel stringTokensPanel = new StringTokensPanel();

    public ResourcesManagementPanel(final BrokerJobModel brokerJobModel) {
        createComponents(brokerJobModel, Sets.<String>newHashSet());
    }

    public ResourcesManagementPanel(final BrokerJobModel brokerJobModel, Set<String> basicResources) {
        createComponents(brokerJobModel, basicResources);
    }

    private void createComponents(final BrokerJobModel brokerJobModel, Set<String> basicResources) {
        Button showAvailableResourcesWindowButton = new Button(getMessage("showWindow"));
        showAvailableResourcesWindowButton.setStyleName(Reindeer.BUTTON_SMALL);
        showAvailableResourcesWindowButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Window w = new Window(getMessage("windowTitle"));
                AvailableResourcesWindowPanel resourcesPanel = new AvailableResourcesWindowPanel(
                        w,
                        brokerJobModel,
                        stringTokensPanel
                );
                w.setContent(resourcesPanel);
                w.setHeight(AvailableResourcesWindowPanel.INITIAL_HEIGHT, Unit.PIXELS);
                w.setWidth(AvailableResourcesWindowPanel.INITIAL_WIDTH, Unit.PIXELS);
                w.center();
                PortalApplication.getCurrent().addWindow(w);
            }
        });

        GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.addComponent(showAvailableResourcesWindowButton, 0, 0);
        gridLayout.setComponentAlignment(showAvailableResourcesWindowButton, Alignment.MIDDLE_CENTER);

        gridLayout.addComponent(stringTokensPanel, 0, 1);
        gridLayout.setComponentAlignment(stringTokensPanel, Alignment.TOP_LEFT);
        gridLayout.setRowExpandRatio(1, 1.f);
        gridLayout.setSizeFull();

        setCompositionRoot(gridLayout);
        setSizeFull();
    }


    private String getMessage(String messageKey) {
        return GlobalState.getMessage(CommonComponentsI18N.ID, "resourcesPanel." + messageKey);
    }
}
