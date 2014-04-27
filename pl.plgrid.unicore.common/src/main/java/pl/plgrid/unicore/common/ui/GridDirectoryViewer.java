package pl.plgrid.unicore.common.ui;

import com.vaadin.ui.Window;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.grid.core.data.u6.U6DataEndpoint;
import eu.unicore.portal.ui.PortalApplication;
import eu.unicore.portal.ui.browser.DataEndpointViewer;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.portal.core.i18n.ComponentsI18N;


public class GridDirectoryViewer extends DataEndpointViewer {
    private final String defaultWindowCaption;

    public GridDirectoryViewer(EndpointReferenceType epr) {
        super(
                new U6DataEndpoint(epr.getAddress().getStringValue())
        );
        defaultWindowCaption = GlobalState
                .getMessage(ComponentsI18N.ID, "gridDirectoryViewer.caption");
    }

    public void showWindow() {
        Window simulationDirectoryWindow = new Window(defaultWindowCaption);
        PortalApplication
                .getCurrent()
                .addWindow(simulationDirectoryWindow);
        simulationDirectoryWindow.setContent(this);
        simulationDirectoryWindow.center();
    }
}
