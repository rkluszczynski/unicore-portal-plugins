package pl.plgrid.unicore.vasp;

import com.vaadin.server.Page;
import com.vaadin.ui.GridLayout;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.ui.views.AbstractView;
import pl.plgrid.unicore.vasp.i18n.VASPViewI18N;

/**
 * @author rkluszczynski
 */
@SuppressWarnings("serial")
public class MainPortalView extends AbstractView {
    private GridLayout mainPage;
    private VASPMainPanel vaspMainPanel;

    public MainPortalView() {
        setTitle(getMessage("title"));

        createMainViewComponents();
        addComponent(mainPage);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible == false) {
            if (vaspMainPanel != null) {
                freeComponentsDuringInvisibility();
            }
        } else {
            refreshComponentsForUserVisibility();
        }
    }

    private void createMainViewComponents() {
        if (mainPage != null) {
            return;
        }
        int browserWindowHeight = Page.getCurrent().getBrowserWindowHeight();
        vaspMainPanel = new VASPMainPanel();
        vaspMainPanel.setHeight(browserWindowHeight, Unit.PIXELS);

        mainPage = new GridLayout(1, 1);
        mainPage.setWidth(100f, Unit.PERCENTAGE);
        mainPage.setHeight(100f, Unit.PERCENTAGE);
        mainPage.addComponent(vaspMainPanel);
    }

    private void refreshComponentsForUserVisibility() {
        vaspMainPanel.setVisible(true);
    }

    private void freeComponentsDuringInvisibility() {
        vaspMainPanel.setVisible(false);
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(VASPViewI18N.ID, "vasp.view." + messageKey);
    }
}
