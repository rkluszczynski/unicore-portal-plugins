package pl.plgrid.unicore.common.impl;

import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.common.services.*;


public class GridServicesExplorerImpl implements GridServicesExplorer {

    private final TargetSystemService targetSystemService;
    private final ServiceOrchestratorService serviceOrchestratorService;
    private final StorageFactoryService storageFactoryService;
    private final GlobalStorageService globalStorageService;
    private final WorkflowFactoryService workflowFactoryService;

    public GridServicesExplorerImpl() {
        targetSystemService = new TargetSystemService();
        serviceOrchestratorService = new ServiceOrchestratorService();
        storageFactoryService = new StorageFactoryService();
        globalStorageService = new GlobalStorageService();
        workflowFactoryService = new WorkflowFactoryService();
    }


    @Override
    public TargetSystemService getTargetSystemService() {
        return targetSystemService;
    }

    @Override
    public ServiceOrchestratorService getServiceOrchestratorService() {
        return serviceOrchestratorService;
    }

    @Override
    public StorageFactoryService getStorageFactoryService() {
        return storageFactoryService;
    }

    @Override
    public GlobalStorageService getGlobalStorageService() {
        return globalStorageService;
    }

    @Override
    public WorkflowFactoryService getWorkflowFactoryService() {
        return workflowFactoryService;
    }
}
