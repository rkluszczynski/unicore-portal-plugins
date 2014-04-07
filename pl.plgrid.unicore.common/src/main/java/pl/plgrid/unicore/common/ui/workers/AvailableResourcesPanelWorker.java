package pl.plgrid.unicore.common.ui.workers;

import com.vaadin.ui.*;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.threads.BackgroundWorker;
import eu.unicore.portal.core.threads.IProgressMonitor;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.GridResourcesExplorer;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;
import pl.plgrid.unicore.common.model.BrokerJobModel;
import pl.plgrid.unicore.common.resources.AvailableBooleanResource;
import pl.plgrid.unicore.common.resources.AvailableEnumResource;
import pl.plgrid.unicore.common.resources.AvailableResource;
import pl.plgrid.unicore.common.ui.AvailableResourcesWindowPanel;

import java.util.Collection;
import java.util.Map;

public class AvailableResourcesPanelWorker extends BackgroundWorker {
    private static final Logger logger = Logger.getLogger(AvailableResourcesPanelWorker.class);

    private GridResourcesExplorer gridResourcesExplorer;
    private AvailableResourcesWindowPanel windowPanel;
    private Collection<AvailableResource> availableResources;
    private final BrokerJobModel brokerJobModel;

    public AvailableResourcesPanelWorker(AvailableResourcesWindowPanel windowPanel,
                                         Collection<AvailableResource> availableResources,
                                         BrokerJobModel brokerJobModel) {
        super(GlobalState.getMessage(CommonComponentsI18N.ID, "resourcesPanel.worker.name"));

        gridResourcesExplorer = Session
                .getCurrent()
                .getServiceRegistry()
                .getService(GridResourcesExplorer.class);
        this.windowPanel = windowPanel;
        this.availableResources = availableResources;
        this.brokerJobModel = brokerJobModel;
    }

    @Override
    protected void work(IProgressMonitor iProgressMonitor) {
        availableResources.clear();
        availableResources.addAll(gridResourcesExplorer.getResources());
    }

    @Override
    protected void updateUI() {
        super.updateUI();

        for (AvailableResource availableResource : availableResources) {
            String resourceName = availableResource.getName();
            String resourceDescription = availableResource.getDescription();

            String resourceValue = getBrokerJobModelResourceValue(resourceName, brokerJobModel);
            boolean initialCheckState = false;
            if (resourceValue == null) {
                resourceValue = availableResource.getDefaultValue();
            } else {
                initialCheckState = true;
            }

            Component component;
            if (availableResource instanceof AvailableEnumResource) {
                AvailableEnumResource enumResource = (AvailableEnumResource) availableResource;
                ComboBox comboBox = new ComboBox("x", enumResource.getAllowed());
                comboBox.setValue(resourceValue);
                component = comboBox;
            } else if (availableResource instanceof AvailableBooleanResource) {
                CheckBox checkBox = new CheckBox("x");
                checkBox.setValue(Boolean.valueOf(resourceValue));
                component = checkBox;
            } else {
                TextField textField = new TextField("x");
                textField.setValue(resourceValue);
                component = textField;
            }
            // TODO: prepare ui component for IntTextField and DoubleTextField

            getTable().addItem(
                    new Object[]{
                            new CheckBox("", initialCheckState),
                            resourceName,
                            resourceDescription,
                            component
                    }
                    , null
            );
        }
        windowPanel.setCaption(getMessage("title.ready"));
    }

    private String getBrokerJobModelResourceValue(String resourceName, BrokerJobModel brokerJobModel) {
        Map<String, String> resourceSet = brokerJobModel.getResourceSet();
        return resourceSet.get(resourceName);
    }


    private Table getTable() {
        return windowPanel.getTable();
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(CommonComponentsI18N.ID, "resourcesPanel." + messageKey);
    }
}
