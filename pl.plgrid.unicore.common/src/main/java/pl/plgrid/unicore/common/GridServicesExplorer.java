package pl.plgrid.unicore.common;

import de.fzj.unicore.uas.client.StorageClient;
import pl.plgrid.unicore.common.services.ServiceOrchestratorService;
import pl.plgrid.unicore.common.services.StorageFactoryService;
import pl.plgrid.unicore.common.services.TargetSystemService;

import java.util.Collection;


public interface GridServicesExplorer {
    TargetSystemService getTargetSystemService();

    ServiceOrchestratorService getServiceOrchestratorService();

    StorageFactoryService getStorageFactoryService();

    Collection<StorageClient> getGlobalStorageServices();

//	Collection<ExecutionService> getExecutionServices(NameFilter... filters);
//
//	Collection<BrokerService> getBrokers();
//		
//	Collection<WorkflowService> getWorkflowServices();
//	
//	Collection<StorageClient> getStorageServices();
//	
//	Collection<AvailableResource> getResources(NameFilter[] resourceFilters, NameFilter[] siteFilters);	
}
