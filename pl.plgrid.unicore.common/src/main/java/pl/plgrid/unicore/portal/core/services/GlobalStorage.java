package pl.plgrid.unicore.portal.core.services;

import pl.plgrid.unicore.portal.core.entities.StorageEntity;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;

import java.util.Collection;

/**
 * Created by Rafal on 2014-05-25.
 */
public class GlobalStorage extends AbstractStorage {
    @Override
    public Collection<StorageEntity> getStorageEntities() throws UnavailableGridServiceException {
        return null;
    }

    @Override
    public <T> T createClient() throws UnavailableGridServiceException {
        return null;
    }
}
