package pl.plgrid.unicore.portal.core.services;

import pl.plgrid.unicore.portal.core.entities.StorageEntity;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;

import java.util.Collection;


abstract class AbstractStorage extends AbstractService {

    abstract
    public Collection<StorageEntity> getStorageEntities() throws UnavailableGridServiceException;

}
