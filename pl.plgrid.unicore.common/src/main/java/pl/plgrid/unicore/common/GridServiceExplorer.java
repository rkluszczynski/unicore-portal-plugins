package pl.plgrid.unicore.common;

import pl.plgrid.unicore.common.services.ServiceOrchestratorService;
import pl.plgrid.unicore.common.services.StorageFactoryService;
import pl.plgrid.unicore.common.services.TargetSystemService;


public interface GridServiceExplorer {
    TargetSystemService getTargetSystemService();

    ServiceOrchestratorService getServiceOrchestratorService();

    StorageFactoryService getStorageFactoryService();

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
