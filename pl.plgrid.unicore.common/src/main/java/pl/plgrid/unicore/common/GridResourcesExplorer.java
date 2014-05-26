package pl.plgrid.unicore.common;

import pl.plgrid.unicore.portal.core.entities.AtomicJobEntity;
import pl.plgrid.unicore.portal.core.entities.StorageEntity;

import java.util.Collection;


public interface GridResourcesExplorer {

    Collection<AtomicJobEntity> getJobs();

    Collection<StorageEntity> getStorages();

    Collection<StorageEntity> getGlobalStorages();

    Collection<StorageEntity> getSiteStorages();

    Collection<StorageEntity> getFactoryStorages();

}
