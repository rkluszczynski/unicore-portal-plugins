package pl.plgrid.unicore.common.ui.panel;

import com.vaadin.server.Page;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.ui.PortalApplication;
import eu.unicore.portal.ui.Styles;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.portal.core.i18n.ComponentsI18N;

public class SimulationLogPanel extends VerticalLayout {
    private final String defaultWindowCaption;

    public SimulationLogPanel(String log) {
        initializeComponents(log);
        defaultWindowCaption = GlobalState
                .getMessage(ComponentsI18N.ID, "simulationLogPanel.caption");
    }

    private void initializeComponents(String text) {
        TextArea textArea = new TextArea();
        textArea.setValue(text);
        textArea.setReadOnly(true);
        textArea.setSizeFull();

        addComponent(textArea);

        int windowHeight = Page.getCurrent().getBrowserWindowHeight();
        windowHeight = (windowHeight < 0) ? 400 : Math.max(windowHeight / 2, 100);
        int windowWidth = Page.getCurrent().getBrowserWindowWidth();
        windowWidth = (windowWidth < 0) ? 600 : Math.max(windowWidth / 2, 100);

        setHeight(windowHeight, Unit.PIXELS);
        setWidth(windowWidth, Unit.PIXELS);
        setMargin(true);
        setSpacing(true);
        addStyleName(Styles.PADDING_All_10);
    }

    public void showWindow() {
        Window simulationDirectoryWindow = new Window(defaultWindowCaption);
        PortalApplication
                .getCurrent()
                .addWindow(simulationDirectoryWindow);
        simulationDirectoryWindow.setContent(this);
        simulationDirectoryWindow.center();
    }

    private static final Logger logger = Logger.getLogger(SimulationLogPanel.class);
}
