package pl.plgrid.unicore.portal.core.states;

import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.resources.AvailableResource;
import pl.plgrid.unicore.portal.core.entities.AtomicJobEntity;
import pl.plgrid.unicore.portal.core.entities.StorageEntity;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.portal.core.services.GlobalStorage;
import pl.plgrid.unicore.portal.core.services.ServiceOrchestrator;
import pl.plgrid.unicore.portal.core.services.StorageFactory;
import pl.plgrid.unicore.portal.core.services.TargetSystem;

import java.util.Collection;

/**
 * Created by Rafal on 2014-05-25.
 */
public class UserGridState {
    private static final Logger logger = Logger.getLogger(UserGridState.class);

    private static final long TTL = 2L * 60L * 60L * 1000L;
    private long lastAccessMillis = System.currentTimeMillis();

    private final TargetSystem targetSystem;
    private final GlobalStorage globalStorage;
    private final StorageFactory storageFactory;
    private final ServiceOrchestrator serviceOrchestrator;

    public UserGridState() {
        targetSystem = new TargetSystem();
        globalStorage = new GlobalStorage();
        storageFactory = new StorageFactory();
        serviceOrchestrator = new ServiceOrchestrator();
    }


    public boolean isExpired() {
        return System.currentTimeMillis() > lastAccessMillis + TTL;
    }

    public void setDirty() {
        lastAccessMillis = System.currentTimeMillis();
    }

    public Collection<StorageEntity> getGlobalStorageEntities() {
        try {
            return globalStorage.getStorageEntities();
        } catch (UnavailableGridServiceException e) {
            // TODO: handle registry unavailability
            logger.error("ERROR: TODO", e);
        }
        return null;
    }

    public Collection<StorageEntity> getStorageFactoryEntities() {
        try {
            return storageFactory.getStorageEntities();
        } catch (UnavailableGridServiceException e) {
            // TODO: handle registry unavailability
            logger.error("ERROR: TODO", e);
        }
        return null;
    }

    public Collection<StorageEntity> getSiteStorageEntities() {
        try {
            return targetSystem.getStorageEntities();
        } catch (UnavailableGridServiceException e) {
            // TODO: handle registry unavailability
            logger.error("ERROR: TODO", e);
        }
        return null;
    }

    public Collection<AvailableResource> getGridResources() {
        return targetSystem.getGridResources();
    }

    public Collection<AtomicJobEntity> getGridAtomicJobsEntities() {
        return targetSystem.getGridJobsEntities();
    }
}
