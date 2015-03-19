package pl.plgrid.unicore.common.ui;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.data.Item;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.threads.BackgroundWorker;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.model.BrokerJobModel;
import pl.plgrid.unicore.common.resources.AvailableResource;
import pl.plgrid.unicore.common.ui.panel.StringTokensPanel;
import pl.plgrid.unicore.common.ui.workers.AvailableResourcesPanelWorker;
import pl.plgrid.unicore.portal.core.i18n.ComponentsI18N;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


public class AvailableResourcesWindowPanel extends CustomComponent {
    private static final Logger logger = Logger.getLogger(AvailableResourcesWindowPanel.class);

    public static final int INITIAL_WIDTH = 600;
    public static final int INITIAL_HEIGHT = 450;

    private Window parentWindow;

    private final Table table = new Table();
    private boolean okButtonClicked = false;

    private final Collection<AvailableResource> availableResources =
            Lists.newArrayList();


    public AvailableResourcesWindowPanel(Window window,
                                         final BrokerJobModel brokerJobModel,
                                         final StringTokensPanel stringTokensPanel,
                                         final Set<String> excludeResourceNames) {
        super();
        parentWindow = window;
        setCaption(getMessage("title.loading"));

        table.addContainerProperty(getMessage("table.column.set"), CheckBox.class, Boolean.FALSE);
        table.addContainerProperty(getMessage("table.column.name"), String.class, null);
        table.addContainerProperty(getMessage("table.column.description"), String.class, null);
        table.addContainerProperty(getMessage("table.column.value"), Component.class, null);

        table.setSelectable(true);
        table.setSizeFull();
//        table.setImmediate(true);

        Button okButton = new Button(getMessage("buttonOk"));
        okButton.setStyleName(Reindeer.BUTTON_SMALL);
        okButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                okButtonClicked = true;
                refreshAvailableResources(brokerJobModel, stringTokensPanel);
                parentWindow.close();
            }
        });
        Button cancelButton = new Button(getMessage("buttonCancel"));
        cancelButton.setStyleName(Reindeer.BUTTON_SMALL);
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                parentWindow.close();
            }
        });

        HorizontalLayout horizontalButtonsLayout = new HorizontalLayout();
        horizontalButtonsLayout.addComponent(okButton);
        horizontalButtonsLayout.addComponent(cancelButton);
        horizontalButtonsLayout.setSpacing(true);

        GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.addComponent(table, 0, 0);
        gridLayout.setRowExpandRatio(0, 1.f);
        gridLayout.addComponent(horizontalButtonsLayout, 0, 1);
        gridLayout.setComponentAlignment(horizontalButtonsLayout, Alignment.MIDDLE_CENTER);
        gridLayout.setSizeFull();
        gridLayout.setMargin(true);

        setCompositionRoot(gridLayout);
        setSizeFull();

        BackgroundWorker worker = new AvailableResourcesPanelWorker(
                this,
                availableResources,
                excludeResourceNames,
                brokerJobModel
        );
        worker.schedule();
    }

    public Table getTable() {
        return table;
    }

    void refreshAvailableResources(BrokerJobModel brokerJobModel, StringTokensPanel stringTokensPanel) {
        Map<String, String> resourceSet = brokerJobModel.getResourceSet();

        resourceSet.clear();
        for (Object o : table.getItemIds()) {
            // Get the current item identifier, which is an integer.
            int iid = (Integer) o;

            // Now get the actual item from the table.
            Item item = table.getItem(iid);

            Boolean isResourceSet =
                    ((CheckBox) item
                            .getItemProperty(getMessage("table.column.set"))
                            .getValue()
                    )
                            .getValue();

            logger.info("QQQ : " + isResourceSet);

            if (isResourceSet) {
                String resourceName = item
                        .getItemProperty(getMessage("table.column.name"))
                        .getValue()
                        .toString();
                String resourceValue = item
                        .getItemProperty(getMessage("table.column.value"))
                        .getValue()
                        .toString();

                logger.info(" ### : " + resourceName + " := " + resourceValue);

//                AvailableResource availableResource = findAvailableResource(resourceName);
                if (resourceName != null) {
                    // TODO: check validation of resource value
                    resourceSet.put(resourceName, resourceValue);
                }
            }
        }
        logger.info(" !!! " + resourceSet.toString());

        for (Map.Entry<String, String> entry : resourceSet.entrySet()) {
            logger.info("TOKEN : " + String.format("%s : %s", entry.getKey(), entry.getValue()));
            stringTokensPanel.putToken(
                    String.format("%s : ", entry.getKey()),
                    String.format("%s : %s", entry.getKey(), entry.getValue())
            );
        }
    }

    private AvailableResource findAvailableResource(String name) {
        for (AvailableResource availableResource : availableResources) {
            if (availableResource.getName().equals(name)) {
                return availableResource;
            }
        }
        return null;
    }

    boolean isOkButtonClicked() {
        return okButtonClicked;
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(ComponentsI18N.ID, "resourcesPanel." + messageKey);
    }
}
