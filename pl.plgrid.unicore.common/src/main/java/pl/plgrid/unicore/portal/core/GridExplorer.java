package pl.plgrid.unicore.portal.core;

import pl.plgrid.unicore.common.resources.AvailableResource;
import pl.plgrid.unicore.portal.core.entities.AtomicJobEntity;
import pl.plgrid.unicore.portal.core.entities.StorageEntity;

import java.util.Collection;

/**
 * Created by Rafal on 2014-04-24.
 */
public interface GridExplorer {

    public Collection<AvailableResource> getResources();

    public Collection<AtomicJobEntity> getJobs();

    public Collection<StorageEntity> getStorages();

    public Collection<StorageEntity> getGlobalStorages();

    public Collection<StorageEntity> getSiteStorages();

    public Collection<StorageEntity> getFactoryStorages();

}
