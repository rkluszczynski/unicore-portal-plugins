package pl.info.rkluszczynski.vaadin.example;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class ExampleUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        Label label = new Label("Hello, Vaadin!");
        setContent(label);
    }
}
