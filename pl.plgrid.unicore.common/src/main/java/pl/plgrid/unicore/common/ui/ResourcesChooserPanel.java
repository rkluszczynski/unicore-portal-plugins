package pl.plgrid.unicore.common.ui;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.ui.PortalApplication;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;

/**
 * Created by Rafal on 2014-04-04.
 */
public class ResourcesChooserPanel extends CustomComponent {


    public ResourcesChooserPanel() {
        Button showAvailableResourcesWindowButton = new Button(getMessage("showWindow"));
        showAvailableResourcesWindowButton.setStyleName(Reindeer.BUTTON_SMALL);
        showAvailableResourcesWindowButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Window w = new Window("Available Resources");
                w.setContent(new AvailableResourcesPanel());
                w.setHeight(AvailableResourcesPanel.INITIAL_HEIGHT, Unit.PIXELS);
                w.setWidth(AvailableResourcesPanel.INITIAL_WIDTH, Unit.PIXELS);
                w.center();
                PortalApplication.getCurrent().addWindow(w);
            }
        });

        TokenPanel tokenPanel = new TokenPanel(
                Lists.newArrayList("A", "BB", "CCC")
        );

        GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.addComponent(showAvailableResourcesWindowButton, 0, 0);
        gridLayout.addComponent(tokenPanel, 0, 1);
        gridLayout.setRowExpandRatio(1, 1.f);
        gridLayout.setSizeFull();

        setCompositionRoot(gridLayout);
        setSizeFull();
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(CommonComponentsI18N.ID, "resourcesPanel." + messageKey);
    }
}
