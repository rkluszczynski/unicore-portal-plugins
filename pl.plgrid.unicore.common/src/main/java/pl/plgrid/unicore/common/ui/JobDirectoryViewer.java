package pl.plgrid.unicore.common.ui;

import com.vaadin.ui.Window;
import eu.unicore.portal.grid.core.data.u6.U6DataEndpoint;
import eu.unicore.portal.ui.PortalApplication;
import eu.unicore.portal.ui.browser.DataEndpointViewer;
import org.w3.x2005.x08.addressing.EndpointReferenceType;


public class JobDirectoryViewer extends DataEndpointViewer {
    private final String defaultWindowCaption;

    public JobDirectoryViewer(EndpointReferenceType epr) {
        super(new U6DataEndpoint(epr.getAddress().getStringValue()));
        defaultWindowCaption = "Job directory (" +
                epr.getAddress().getStringValue().split("=")[1] + ")";
    }

    public void showWindow() {
        Window jobDirectoryWindow = new Window(defaultWindowCaption);
        PortalApplication
                .getCurrent()
                .addWindow(jobDirectoryWindow);
        jobDirectoryWindow.setContent(this);
        jobDirectoryWindow.center();
    }
}
