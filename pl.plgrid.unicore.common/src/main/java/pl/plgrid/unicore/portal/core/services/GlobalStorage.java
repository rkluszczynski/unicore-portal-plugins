package pl.plgrid.unicore.portal.core.services;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import de.fzj.unicore.uas.StorageManagement;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.portal.core.entities.StorageEntity;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;

import java.util.Collection;
import java.util.List;

import static pl.plgrid.unicore.portal.core.entities.StorageEntityType.GLOBAL_STORAGE;

/**
 * Created by Rafal on 2014-05-25.
 */
public class GlobalStorage extends AbstractStorage {
    @Override
    public Collection<StorageEntity> getStorageEntities() throws UnavailableGridServiceException {
        List<EndpointReferenceType> accessibleServices = getAccessibleServices(StorageManagement.SMS_PORT);
        return Collections2.transform(accessibleServices,
                new Function<EndpointReferenceType, StorageEntity>() {
                    @Override
                    public StorageEntity apply(EndpointReferenceType storageEpr) {
                        return new StorageEntity(storageEpr, GLOBAL_STORAGE);
                    }
                }
        );
    }
}
