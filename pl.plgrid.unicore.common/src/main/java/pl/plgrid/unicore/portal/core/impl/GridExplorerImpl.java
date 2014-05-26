package pl.plgrid.unicore.portal.core.impl;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import eu.unicore.portal.core.GlobalState;
import pl.plgrid.unicore.common.resources.AvailableResource;
import pl.plgrid.unicore.portal.core.GridExplorer;
import pl.plgrid.unicore.portal.core.entities.AtomicJobEntity;
import pl.plgrid.unicore.portal.core.entities.StorageEntity;
import pl.plgrid.unicore.portal.core.states.UsersGridStatesCache;

import java.util.Collection;
import java.util.List;

/**
 * Created by Rafal on 2014-04-24.
 */
public class GridExplorerImpl implements GridExplorer {
    private final UsersGridStatesCache usersGridStatesCache;

    public GridExplorerImpl() {
        Object uPortal = GlobalState.getCurrent().getSharedObjects().get("uPortal");
        usersGridStatesCache = new UsersGridStatesCache();
    }

    @Override
    public Collection<AvailableResource> getResources() {
        return usersGridStatesCache
                .getUserGridState()
                .getGridResources();
    }

    @Override
    public Collection<AtomicJobEntity> getJobs() {
        return usersGridStatesCache
                .getUserGridState()
                .getGridAtomicJobsEntities();
    }

    @Override
    public Collection<StorageEntity> getStorages() {
        List<StorageEntity> storageClients = Lists.newArrayList();

        Collection<StorageEntity> globalStorages = getGlobalStorages();
        if (globalStorages != null && !globalStorages.isEmpty()) {
            storageClients.addAll(globalStorages);
        }

        Collection<StorageEntity> siteStorages = getSiteStorages();
        if (siteStorages != null && !siteStorages.isEmpty()) {
            storageClients.addAll(siteStorages);
        }

        Collection<StorageEntity> factoryStorages = getFactoryStorages();
        if (factoryStorages != null && !factoryStorages.isEmpty()) {
            storageClients.addAll(factoryStorages);
        }
        return storageClients;
    }

    @Override
    public Collection<StorageEntity> getGlobalStorages() {
        return usersGridStatesCache
                .getUserGridState()
                .getGlobalStorageEntities();
    }

    @Override
    public Collection<StorageEntity> getSiteStorages() {
        return usersGridStatesCache
                .getUserGridState()
                .getSiteStorageEntities();
    }

    @Override
    public Collection<StorageEntity> getFactoryStorages() {
        return usersGridStatesCache
                .getUserGridState()
                .getStorageFactoryEntities();
    }
}
