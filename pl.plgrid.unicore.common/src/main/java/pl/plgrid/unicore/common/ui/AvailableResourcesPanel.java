package pl.plgrid.unicore.common.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.threads.BackgroundWorker;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;
import pl.plgrid.unicore.common.ui.workers.AvailableResourcesPanelWorker;


public class AvailableResourcesPanel extends CustomComponent {
    private static final Logger logger = Logger.getLogger(AvailableResourcesPanel.class);

    public static final int INITIAL_WIDTH = 600;
    public static final int INITIAL_HEIGHT = 400;

    private Table table;


    public AvailableResourcesPanel() {
        super();
        setCaption(getMessage("title"));

        table = new Table(getMessage("table.title"));

        table.addContainerProperty(getMessage("table.column.set"), CheckBox.class, Boolean.FALSE);
        table.addContainerProperty(getMessage("table.column.name"), String.class, null);
        table.addContainerProperty(getMessage("table.column.default"), String.class, null);
        table.addContainerProperty(getMessage("table.column.type"), String.class, null);
        table.addContainerProperty(getMessage("table.column.value"), Component.class, null);

        table.setSelectable(true);
        table.setSizeFull();
//        table.setImmediate(true);

        Button okButton = new Button(getMessage("buttonOk"));
        okButton.setStyleName(Reindeer.BUTTON_SMALL);
        Button cancelButton = new Button(getMessage("buttonCancel"));
        cancelButton.setStyleName(Reindeer.BUTTON_SMALL);
        HorizontalLayout horizontalButtonsLayout = new HorizontalLayout();
        horizontalButtonsLayout.addComponent(okButton);
        horizontalButtonsLayout.addComponent(cancelButton);
        horizontalButtonsLayout.setSpacing(true);

        GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.addComponent(table, 0, 0);
        gridLayout.addComponent(horizontalButtonsLayout, 0, 1);
        gridLayout.setRowExpandRatio(0, 1.f);
        gridLayout.setSizeFull();
        gridLayout.setMargin(true);

        setCompositionRoot(gridLayout);
        setSizeFull();

        BackgroundWorker worker = new AvailableResourcesPanelWorker(table);
        worker.schedule();
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(CommonComponentsI18N.ID, "resourcesPanel." + messageKey);
    }
}
