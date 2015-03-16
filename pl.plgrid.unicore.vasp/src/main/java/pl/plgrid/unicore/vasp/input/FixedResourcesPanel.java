package pl.plgrid.unicore.vasp.input;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import eu.unicore.portal.core.GlobalState;
import pl.plgrid.unicore.common.ui.model.ResourceSetComponent;
import pl.plgrid.unicore.vasp.i18n.VASPViewI18N;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rafal on 2015-03-16.
 */
public class FixedResourcesPanel extends CustomComponent implements ResourceSetComponent {

    private final ComboBox queueComboBox;
    private ObjectProperty<String> projectProperty = new ObjectProperty<String>("");
    private ObjectProperty<Integer> memoryProperty = new ObjectProperty<Integer>(512);
    private ObjectProperty<Integer> nodesProperty = new ObjectProperty<Integer>(1);
    private ObjectProperty<Integer> cpusProperty = new ObjectProperty<Integer>(4);


    public FixedResourcesPanel() {
        GridLayout gridLayout = new GridLayout(1, 6);
        int gridLayoutRowNumber = 0;

        TextField projectTextField = new TextField("Project: ", projectProperty);
        projectTextField.setMaxLength(32);
        FormLayout projectFormLayout = createComponentFormLayout(projectTextField);
        gridLayout.addComponent(projectFormLayout, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(projectFormLayout, Alignment.MIDDLE_RIGHT);

        ++gridLayoutRowNumber;
        queueComboBox = new ComboBox("Queue: ", Collections2.transform(
                Arrays.asList(PLGridQueues.values()), new Function<PLGridQueues, String>() {
                    @Override
                    public String apply(PLGridQueues input) {
                        return input.getQueueName();
                    }
                })
        );
        queueComboBox.setTextInputAllowed(false);
        queueComboBox.setNullSelectionAllowed(false);
        queueComboBox.setValue(PLGridQueues.PLGRID.getQueueName());
        FormLayout queueFormLayout = createComponentFormLayout(queueComboBox);
        gridLayout.addComponent(queueFormLayout, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(queueFormLayout, Alignment.MIDDLE_RIGHT);

        ++gridLayoutRowNumber;
        TextField memoryTextField = new TextField("Memory [MB]: ", memoryProperty);
        memoryTextField.setMaxLength(6);
        memoryTextField.setConverter(Integer.class);
        memoryTextField.addValidator(new IntegerRangeValidator("Number should be positive", 1, 999999));
        FormLayout memoryFormLayout = createComponentFormLayout(memoryTextField);
        gridLayout.addComponent(memoryFormLayout, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(memoryFormLayout, Alignment.MIDDLE_RIGHT);

        ++gridLayoutRowNumber;
        TextField nodesTextField = new TextField("Number of nodes: ", nodesProperty);
        nodesTextField.setMaxLength(3);
        nodesTextField.setConverter(Integer.class);
        nodesTextField.addValidator(new IntegerRangeValidator("Number should be positive", 1, 99));
        FormLayout nodesFormLayout = createComponentFormLayout(nodesTextField);
        gridLayout.addComponent(nodesFormLayout, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(nodesFormLayout, Alignment.MIDDLE_RIGHT);

        ++gridLayoutRowNumber;
        TextField cpusTextField = new TextField("CPUs per node: ", cpusProperty);
        cpusTextField.setMaxLength(2);
        cpusTextField.setConverter(Integer.class);
        cpusTextField.addValidator(new IntegerRangeValidator("Number should be positive", 1, 64));
        FormLayout cpusFormLayout = createComponentFormLayout(cpusTextField);
        gridLayout.addComponent(cpusFormLayout, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(cpusFormLayout, Alignment.MIDDLE_RIGHT);

        ++gridLayoutRowNumber;
        gridLayout.addComponent(new Label(""));

        gridLayout.setMargin(new MarginInfo(true, true, true, true));
        gridLayout.setSizeFull();

        setCompositionRoot(gridLayout);
        setSizeFull();
    }


    private FormLayout createComponentFormLayout(AbstractComponent component) {
        component.setWidth(0.5f * component.getWidth(), component.getWidthUnits());

        FormLayout formLayout = new FormLayout(component);
        formLayout.setSpacing(false);
        formLayout.setMargin(false);
        formLayout.setSizeUndefined();
        formLayout.setComponentAlignment(component, Alignment.MIDDLE_RIGHT);
        return formLayout;
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(VASPViewI18N.ID, "vasp.caption." + messageKey);
    }

    @Override
    public Map<String, String> getResources() {
        HashMap<String, String> resources = Maps.newHashMap();

        String queueName = (String) queueComboBox.getValue();
        resources.put("Queue", queueName);
        if (PLGridQueues.PLGRID_LONG.getQueueName().equals(queueName)) {
            resources.put("Runtime", PLGridQueues.PLGRID_LONG.getQueueLimit());
        } else if (PLGridQueues.PLGRID.getQueueName().equals(queueName)) {
            resources.put("Runtime", PLGridQueues.PLGRID.getQueueLimit());
        } else {
            resources.put("Runtime", PLGridQueues.PLGRID_TESTING.getQueueLimit());
        }

        String project = projectProperty.getValue();
        if (!Strings.isNullOrEmpty(project)) {
            resources.put("Project", project);
        }
//        Reservation

        long memoryInBytes = memoryProperty.getValue().longValue() * 1024L * 1024L;
        resources.put("Memory", String.valueOf(memoryInBytes));
        resources.put("CPUsPerNode", cpusProperty.getValue().toString());
        resources.put("Nodes", nodesProperty.getValue().toString());
        return resources;
    }

    private enum PLGridQueues {
        PLGRID_TESTING("plgrid-testing", 3600L),
        PLGRID("plgrid", 259200L),
        PLGRID_LONG("plgrid-long", 604800L);

        private PLGridQueues(String queueName, long queueLimit) {
            this.queueName = queueName;
            this.queueLimit = queueLimit;
        }

        public String getQueueName() {
            return queueName;
        }

        public String getQueueLimit() {
            return String.valueOf(queueLimit);
        }

        private final String queueName;
        private final long queueLimit;
    }
}
