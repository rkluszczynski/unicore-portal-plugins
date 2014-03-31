package pl.plgrid.unicore.common.ui.workers;

import com.vaadin.ui.Table;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.threads.BackgroundWorker;
import eu.unicore.portal.core.threads.IProgressMonitor;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.GridResourcesExplorer;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;
import pl.plgrid.unicore.common.resources.AvailableResource;

import java.util.Collection;

public class AvailableResourcesPanelWorker extends BackgroundWorker {
    private static final Logger logger = Logger.getLogger(AvailableResourcesPanelWorker.class);

    private GridResourcesExplorer gridResourcesExplorer;
    private Table table;

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
        Collection<AvailableResource> availableResources = gridResourcesExplorer.getResources();
        for (AvailableResource availableResource : availableResources) {

            String resourceName = availableResource.getName();
//            String resourceType = availableResource.

//
//            logger.info("");
//
//
//            log.info("RESOURCE: " + resName + " -> " + resType + " / "
//                    + attrs.size());
//
//            Component component = null;
//            if (resType.intValue() == AvailableResourceTypeType.INT_CHOICE) {
//                List<String> valuesList = Lists.newArrayList();
//                for (AvailableResourceType art : attrs) {
//                    valuesList.addAll(Arrays.asList(art
//                            .getAllowedValueArray()));
//                }
//                ComboBox comboBox = new ComboBox("x", valuesList);
//                comboBox.setValue(attrs.get(0).getDefault());
//                component = comboBox;
//            } else if (resType.intValue() == AvailableResourceTypeType.INT_BOOLEAN) {
//                CheckBox checkBox = new CheckBox("x");
//                checkBox.setValue(attrs.get(0).isSetDefault());
//                component = checkBox;
//            } else {
//                component = new TextField(attrs.get(0).getDefault());
//            }
//
//            getTable().addItem(
//                    new Object[]{
//                            resName,
//                            attrs.get(0).getDefault(),
//                            resType.toString(),
//                            component}
//                    , null
//            );
//

        }

    }

    private Table getTable() {
        return table;
    }
}
