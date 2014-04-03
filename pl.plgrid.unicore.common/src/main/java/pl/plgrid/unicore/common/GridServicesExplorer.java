package pl.plgrid.unicore.common;

import pl.plgrid.unicore.common.services.*;


public interface GridServicesExplorer {

    //	Collection<ExecutionService> getExecutionServices(NameFilter... filters);
    TargetSystemService getTargetSystemService();


    //	Collection<BrokerService> getBrokers();
    ServiceOrchestratorService getServiceOrchestratorService();


    //	Collection<StorageClient> getStorageServices();
    StorageFactoryService getStorageFactoryService();

    GlobalStorageService getGlobalStorageService();


    //	Collection<WorkflowService> getWorkflowServices();
    WorkflowFactoryService getWorkflowFactoryService();

}
