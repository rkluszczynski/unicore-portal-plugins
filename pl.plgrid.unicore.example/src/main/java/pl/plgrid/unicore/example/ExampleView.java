package pl.plgrid.unicore.example;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.User;
import eu.unicore.portal.ui.Styles;
import eu.unicore.portal.ui.views.AbstractView;
import pl.plgrid.unicore.example.i18n.ExampleI18N;
import pl.plgrid.unicore.example.panel.ExamplePanel;

/**
 * @author rkluszczynski
 */
@SuppressWarnings("serial")
public class ExampleView extends AbstractView {
    private GridLayout mainPage;
    private VerticalLayout mainContainer;

    public ExampleView() {
        setTitle(GlobalState.getMessage(ExampleI18N.ID, "plgExampleView.title")); //$NON-NLS-1$

        createMainViewComponents();
        addComponent(mainPage);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            prepareComponentsOnlyForVisibleState();
        } else {
            // remove all unneeded components (in this case: all)
            if (mainContainer != null) {
                mainContainer.removeAllComponents();
            }
        }
    }

    private void createMainViewComponents() {
        if (mainPage != null) {
            return;
        }
        mainPage = new GridLayout(1, 1);
        mainPage.setWidth(100f, Unit.PERCENTAGE);

        mainContainer = new VerticalLayout();
        mainContainer.addStyleName(Styles.PADDING_All_10);

        mainPage.addComponent(mainContainer);
    }

    private void prepareComponentsOnlyForVisibleState() {
        User user = Session.getCurrent().getUser();
        ExamplePanel examplePanel = new ExamplePanel(user);

        mainContainer.addComponent(new Label("<hr />", ContentMode.HTML));
        mainContainer.addComponent(examplePanel);
    }
}
