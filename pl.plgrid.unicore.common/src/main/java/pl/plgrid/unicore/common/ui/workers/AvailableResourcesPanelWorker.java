package pl.plgrid.unicore.common.ui.workers;

import com.vaadin.ui.*;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.threads.BackgroundWorker;
import eu.unicore.portal.core.threads.IProgressMonitor;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.GridResourcesExplorer;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;
import pl.plgrid.unicore.common.resources.AvailableBooleanResource;
import pl.plgrid.unicore.common.resources.AvailableEnumResource;
import pl.plgrid.unicore.common.resources.AvailableResource;

import java.util.Collection;

public class AvailableResourcesPanelWorker extends BackgroundWorker {
    private static final Logger logger = Logger.getLogger(AvailableResourcesPanelWorker.class);

    private GridResourcesExplorer gridResourcesExplorer;
    private Table table;

    private Collection<AvailableResource> availableResources;


    public AvailableResourcesPanelWorker(Table table) {
        super(GlobalState.getMessage(CommonComponentsI18N.ID, "resourcesPanel.worker.name"));
        gridResourcesExplorer = Session
                .getCurrent()
                .getServiceRegistry()
                .getService(GridResourcesExplorer.class);
        this.table = table;
    }

    @Override
    protected void work(IProgressMonitor iProgressMonitor) {
        availableResources = gridResourcesExplorer.getResources();
    }

    @Override
    protected void updateUI() {
        super.updateUI();

        for (AvailableResource availableResource : availableResources) {

            String resourceName = availableResource.getName();
            String resourceDefaultValue = availableResource.getDefaultValue();
            String resourceTypeString = availableResource.getClass().toString();

            Component component;
            if (availableResource instanceof AvailableEnumResource) {
                AvailableEnumResource enumResource = (AvailableEnumResource) availableResource;
                ComboBox comboBox = new ComboBox("x", enumResource.getAllowed());
                comboBox.setValue(enumResource.getDefaultValue());
                component = comboBox;
            } else if (availableResource instanceof AvailableBooleanResource) {
                CheckBox checkBox = new CheckBox("x");
                checkBox.setValue(Boolean.valueOf(availableResource.getDefaultValue()));
                component = checkBox;
            } else {
                component = new TextField(availableResource.getDefaultValue());
            }
            // TODO: prepare ui component for IntTextField and DoubleTextField

            getTable().addItem(
                    new Object[]{
                            new CheckBox(),
                            resourceName,
                            resourceDefaultValue,
                            resourceTypeString,
                            component
                    }
                    , null
            );
        }
    }


    private Table getTable() {
        return table;
    }
}
