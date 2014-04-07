package pl.plgrid.unicore.vasp.input;

import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.vaadin.ui.*;
import eu.unicore.portal.ui.Styles;
import pl.plgrid.unicore.common.ui.model.ResourceSetComponent;

import java.util.Map;

/**
 * @author Rafal
 */
public class ResourcesOnTopPanel extends CustomComponent implements ResourceSetComponent {

    private TextField projectTextField;

    public ResourcesOnTopPanel() {
        super();
        GridLayout gridLayout = createComponents();

        setCompositionRoot(gridLayout);
        setSizeFull();
    }

    private GridLayout createComponents() {
        GridLayout gridLayout = new GridLayout(1, 1);
        int componentRowNumber = 0;

        projectTextField = new TextField("Project :");
        projectTextField.addStyleName(Styles.MARGIN_TOP_BOTTOM_15);
        FormLayout projectFormLayout = new FormLayout();
        projectFormLayout.setSpacing(true);
        projectFormLayout.addComponent(projectTextField);
        gridLayout.addComponent(projectFormLayout, 0, componentRowNumber);
        gridLayout.setComponentAlignment(projectFormLayout, Alignment.MIDDLE_LEFT);

        return gridLayout;
    }


    @Override
    public Map<String, String> getResources() {
        Map<String, String> resources = Maps.newHashMap();
        String projectTextFieldValue = projectTextField.getValue();
        resources.put("Project", projectTextFieldValue == null ? "" : projectTextFieldValue);
        return resources;
    }
}
