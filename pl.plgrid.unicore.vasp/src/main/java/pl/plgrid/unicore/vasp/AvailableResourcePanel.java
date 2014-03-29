package pl.plgrid.unicore.vasp;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.unigrids.services.atomic.types.AvailableResourceType;
import org.unigrids.services.atomic.types.AvailableResourceTypeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author rkluszczynski
 */
@SuppressWarnings("serial")
public class AvailableResourcePanel extends Panel {

    private static final Logger log = Logger
            .getLogger(AvailableResourcePanel.class);

    public static final int INITIAL_WIDTH = 550;
    public static final int INITIAL_HEIGHT = 300;

    private Table table;

    public AvailableResourcePanel() {
        super();

        table = new Table("Grid Availabe Resources");

        setContent(table);
        table.addContainerProperty("Resource", String.class, null);
        table.addContainerProperty("Default", String.class, null);
        table.addContainerProperty("Type", String.class, null);
        table.addContainerProperty("Value", Component.class, null);

        table.setSelectable(true);
        table.setSizeFull();
        table.setImmediate(true);

        VerticalLayout content = new VerticalLayout();
		// content.addComponent(menuBar);
        // content.addComponent(header);
        content.addComponent(table);
        // content.addComponent(footer);
        content.setMargin(true);

        setContent(content);
        setImmediate(true);
        setHeight(INITIAL_HEIGHT, Unit.PIXELS);
        setWidth(INITIAL_WIDTH, Unit.PIXELS);
    }

    public Table getTable() {
        return table;
    }

    public void setResources(
            ConcurrentMap<String, ConcurrentHashMap<AvailableResourceTypeType.Enum, ArrayList<AvailableResourceType>>> m) {
        for (String resName : m.keySet()) {
            for (AvailableResourceTypeType.Enum resType : m.get(resName).keySet()) {
                List<AvailableResourceType> attrs = m.get(resName).get(resType);
                log.info("RESOURCE: " + resName + " -> " + resType + " / "
                        + attrs.size());

                Component component = null;
                if (resType.intValue() == AvailableResourceTypeType.INT_CHOICE) {
                    List<String> valuesList = Lists.newArrayList();
                    for (AvailableResourceType art : attrs) {
                        valuesList.addAll(Arrays.asList(art
                                .getAllowedValueArray()));
                    }
                    ComboBox comboBox = new ComboBox("x", valuesList);
                    comboBox.setValue(attrs.get(0).getDefault());
                    component = comboBox;
                } else if (resType.intValue() == AvailableResourceTypeType.INT_BOOLEAN) {
                    CheckBox checkBox = new CheckBox("x");
                    checkBox.setValue(attrs.get(0).isSetDefault());
                    component = checkBox;
                } else {
                    component = new TextField(attrs.get(0).getDefault());
                }

                getTable().addItem(
                        new Object[]{
                            resName, 
                            attrs.get(0).getDefault(),
                            resType.toString(), 
                            component}
                        , null
                );
            }
        }
    }
}
