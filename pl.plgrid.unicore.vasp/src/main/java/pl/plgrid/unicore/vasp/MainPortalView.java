package pl.plgrid.unicore.vasp;

import com.vaadin.server.Page;
import com.vaadin.ui.GridLayout;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.ui.views.AbstractView;
import pl.plgrid.unicore.vasp.i18n.VASPViewI18N;

/**
 *
 * @author rkluszczynski
 */
@SuppressWarnings("serial")
public class MainPortalView extends AbstractView {

    private GridLayout mainPage;
    VASPMainPanel vaspMainPanel;

    public MainPortalView() {
        setTitle(GlobalState.getMessage(VASPViewI18N.ID, "vaspView.title")); //$NON-NLS-1$

        createMainViewComponents();
        addComponent(mainPage);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible == false) {
            if (vaspMainPanel != null) {
                freeComponentsDuringUnvisibility();
            }
        } else {
            refreshComponentsForUserVisibility();
        }
    }

    private void createMainViewComponents() {
        if (mainPage != null) {
            return;
        }
        mainPage = new GridLayout(1, 1);
        mainPage.setWidth(100f, Unit.PERCENTAGE);
        mainPage.setHeight(100f, Unit.PERCENTAGE);

        int browserWindowHeight = Page.getCurrent().getBrowserWindowHeight();        
        vaspMainPanel = new VASPMainPanel();
        vaspMainPanel.setHeight(browserWindowHeight, Unit.PIXELS);
        mainPage.addComponent(vaspMainPanel);
    }

    private void refreshComponentsForUserVisibility() {
        vaspMainPanel.setVisible(true);
    }

    private void freeComponentsDuringUnvisibility() {
        vaspMainPanel.setVisible(false);
    }
}
