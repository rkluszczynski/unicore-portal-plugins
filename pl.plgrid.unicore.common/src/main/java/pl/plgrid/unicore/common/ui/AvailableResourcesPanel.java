package pl.plgrid.unicore.common.ui;

import com.vaadin.ui.*;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.threads.BackgroundWorker;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;
import pl.plgrid.unicore.common.ui.workers.AvailableResourcesPanelWorker;


public class AvailableResourcesPanel extends Panel {

    private static final Logger logger = Logger.getLogger(AvailableResourcesPanel.class);

    public static final int INITIAL_WIDTH = 550;
    public static final int INITIAL_HEIGHT = 300;

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
        table.setImmediate(true);
        setContent(table);

        VerticalLayout content = new VerticalLayout();
        // content.addComponent(menuBar);
        // content.addComponent(header);
        content.addComponent(table);
        // content.addComponent(footer);
        content.setMargin(true);

        setContent(content);
        setImmediate(true);
        setHeight(INITIAL_HEIGHT, Unit.PIXELS);
        setWidth(INITIAL_WIDTH, Unit.PIXELS);

        BackgroundWorker worker = new AvailableResourcesPanelWorker(table);
        worker.schedule();
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(CommonComponentsI18N.ID, "resourcesPanel." + messageKey);
    }
}
