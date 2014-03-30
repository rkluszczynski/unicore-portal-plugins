package pl.plgrid.unicore.example.panel;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import eu.unicore.portal.core.User;
import org.apache.log4j.Logger;

/**
 * @author rkluszczynski
 */
public class ExamplePanel extends HorizontalLayout {
    private static final Logger logger = Logger.getLogger(ExamplePanel.class);

    private final User user;

    public ExamplePanel(User user) {
        logger.debug("Creating ExamplePanel for user:" + user);

        this.user = user;
        createPanelComponents();
    }

    private void createPanelComponents() {
        setSizeFull();
//        setImmediate(true);
        addComponent(new Label("Username:"));

        TextField usernameTextField = new TextField();
        usernameTextField.setValue(user.getUsername());
        usernameTextField.setEnabled(false);
        addComponent(usernameTextField);
    }
}
