package pl.plgrid.unicore.portal.core.services;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import de.fzj.unicore.uas.client.StorageFactoryClient;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.portal.core.entities.StorageEntity;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;

import java.util.Collection;
import java.util.List;

import static pl.plgrid.unicore.portal.core.entities.StorageEntityType.FACTORY_STORAGE;

/**
 * Created by Rafal on 2014-05-25.
 */
public class StorageFactory extends AbstractStorage {
    @Override
    public Collection<StorageEntity> getStorageEntities() throws UnavailableGridServiceException {
        List<StorageEntity> storageEntities = Lists.newArrayList();

        List<EndpointReferenceType> accessibleServices = getAccessibleServices(de.fzj.unicore.uas.StorageFactory.SMF_PORT);
        for (EndpointReferenceType accessibleService : accessibleServices) {
            try {
                // FIXME: is there a way to get rid of client instance to get storage EPRs
                StorageFactoryClient sfc = new StorageFactoryClient(
                        accessibleService, SecurityHelper.getClientConfig());
                storageEntities.addAll(
                        Collections2.transform(sfc.getAccessibleStorages(),
                                new Function<EndpointReferenceType, StorageEntity>() {
                                    @Override
                                    public StorageEntity apply(EndpointReferenceType storageEpr) {
                                        return new StorageEntity(storageEpr, FACTORY_STORAGE);
                                    }
                                }
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return storageEntities;
    }

    @Override
    public <T> T createClient() throws UnavailableGridServiceException {
        return null;
    }
}
