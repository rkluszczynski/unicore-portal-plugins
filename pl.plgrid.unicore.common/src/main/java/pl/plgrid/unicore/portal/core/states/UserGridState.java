package pl.plgrid.unicore.portal.core.states;

import pl.plgrid.unicore.portal.core.entities.StorageEntity;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.portal.core.services.GlobalStorage;
import pl.plgrid.unicore.portal.core.services.StorageFactory;

import java.util.Collection;

/**
 * Created by Rafal on 2014-05-25.
 */
public class UserGridState {
    private static final long TTL = 2L * 60L * 60L * 1000L;
    private long lastAccessMillis = System.currentTimeMillis();

    private final GlobalStorage globalStorage;
    private final StorageFactory storageFactory;

    public UserGridState() {
        globalStorage = new GlobalStorage();
        storageFactory = new StorageFactory();
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
            e.printStackTrace();
            return null;
        }
    }

    public Collection<StorageEntity> getStorageFactoryEntities() {
        try {
            return storageFactory.getStorageEntities();
        } catch (UnavailableGridServiceException e) {
            // TODO: handle registry unavailability
            e.printStackTrace();
            return null;
        }
    }
}
