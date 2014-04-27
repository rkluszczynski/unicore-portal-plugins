package pl.plgrid.unicore.common;

import pl.plgrid.unicore.common.resources.AvailableResource;
import pl.plgrid.unicore.portal.core.entities.AtomicJobEntity;
import pl.plgrid.unicore.portal.core.entities.StorageEntity;

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
