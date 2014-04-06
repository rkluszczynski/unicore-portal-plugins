package pl.plgrid.unicore.example.panel;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.User;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.example.i18n.ExampleI18N;

/**
 * @author rkluszczynski
 */
public class ExamplePanel extends HorizontalLayout {
    private static final Logger logger = Logger.getLogger(ExamplePanel.class);
    private final User user;

    public ExamplePanel(User user) {
        logger.debug(String.format("Creating ExamplePanel for user: <%s>", user));

        this.user = user;
        createPanelComponents();
    }

    private void createPanelComponents() {
        Label usernameLabel = new Label(
                GlobalState.getMessage(ExampleI18N.ID, "plgExampleView.usernameLabel")
        );
        addComponent(usernameLabel);
        setComponentAlignment(usernameLabel, Alignment.TOP_RIGHT);

        TextField usernameTextField = new TextField();
        usernameTextField.setValue(user.getUsername());
        usernameTextField.setEnabled(false);
        addComponent(usernameTextField);
        setComponentAlignment(usernameTextField, Alignment.TOP_LEFT);

        setSizeFull();
    }
}
