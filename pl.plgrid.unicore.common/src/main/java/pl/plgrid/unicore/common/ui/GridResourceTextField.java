package pl.plgrid.unicore.common.ui;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;
import pl.plgrid.unicore.common.ui.model.ResourceComponent;

/**
 * @author Rafal
 */
public class GridResourceTextField extends TextField implements ResourceComponent {
    private String resourceName;

    public GridResourceTextField(String caption, String value, String resourceName) {
        super(caption, value);
        createComponent(resourceName);
    }

    public GridResourceTextField(String caption, Property dataSource, String resourceName) {
        super(caption, dataSource);
        createComponent(resourceName);
    }

    public GridResourceTextField(Property dataSource, String resourceName) {
        super(dataSource);
        createComponent(resourceName);
    }

    public GridResourceTextField(String caption, String resourceName) {
        super(caption);
        createComponent(resourceName);
    }

    public GridResourceTextField(String resourceName) {
        createComponent(resourceName);
    }

    private void createComponent(String resourceName) {
        this.resourceName = resourceName;
    }


    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public String getResourceValue() {
        return getValue();
    }
}
