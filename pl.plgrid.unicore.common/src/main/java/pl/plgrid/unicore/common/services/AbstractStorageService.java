package pl.plgrid.unicore.common.services;

import pl.plgrid.unicore.common.entities.StorageEntity;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;

import java.util.Collection;


abstract class AbstractStorageService extends AbstractService {

    abstract public Collection<StorageEntity> getStorageEntities() throws UnavailableGridServiceException;

}
