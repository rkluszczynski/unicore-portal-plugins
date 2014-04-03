package pl.plgrid.unicore.common;

import pl.plgrid.unicore.common.entities.AtomicJobEntity;
import pl.plgrid.unicore.common.entities.StorageEntity;
import pl.plgrid.unicore.common.resources.AvailableResource;

import java.util.Collection;


public interface GridResourcesExplorer {

    //	Collection<AvailableResource> getResources(NameFilter[] resourceFilters, NameFilter[] siteFilters);
    Collection<AvailableResource> getResources();

    Collection<AtomicJobEntity> getJobs();

    Collection<StorageEntity> getStorages();

    Collection<StorageEntity> getGlobalStorages();

    Collection<StorageEntity> getSiteStorages();

    Collection<StorageEntity> getFactoryStorages();

}
