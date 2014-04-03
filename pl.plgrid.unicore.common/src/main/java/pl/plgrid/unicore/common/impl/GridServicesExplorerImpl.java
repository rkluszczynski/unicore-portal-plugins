package pl.plgrid.unicore.common.impl;

import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.common.services.ServiceOrchestratorService;
import pl.plgrid.unicore.common.services.StorageFactoryService;
import pl.plgrid.unicore.common.services.TargetSystemService;


public class GridServicesExplorerImpl implements GridServicesExplorer {

    private final TargetSystemService targetSystemService;
    private final ServiceOrchestratorService serviceOrchestratorService;
    private final StorageFactoryService storageFactoryService;

    public GridServicesExplorerImpl() {
        targetSystemService = new TargetSystemService();
        serviceOrchestratorService = new ServiceOrchestratorService();
        storageFactoryService = new StorageFactoryService();
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

}
