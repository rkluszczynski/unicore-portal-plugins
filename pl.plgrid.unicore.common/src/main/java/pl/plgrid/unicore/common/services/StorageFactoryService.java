package pl.plgrid.unicore.common.services;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import de.fzj.unicore.uas.StorageFactory;
import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.uas.client.StorageFactoryClient;
import eu.unicore.portal.core.Session;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.entities.StorageEntity;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.common.utils.SecurityHelper;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static pl.plgrid.unicore.common.entities.StorageEntityType.FACTORY_STORAGE;

public class StorageFactoryService extends AbstractStorageService {

    @Override
    public StorageClient createClient() throws UnavailableGridServiceException {
        return getServiceClient(StorageFactory.SMF_PORT);
    }

    private StorageClient getServiceClient(QName port) throws UnavailableGridServiceException {
        List<EndpointReferenceType> accessibleServices = getAccessibleServices(port);

        int i = new Random().nextInt(accessibleServices
                .size());
        EndpointReferenceType storageFactoryEpr = accessibleServices
                .get(i);

        logger.info("Using Storage Factory: <"
                + storageFactoryEpr.getAddress().getStringValue()
                + "> by user: <" + Session.getCurrent().getUser().getUsername()
                + ">");
        // TODO: check if setReceiver is needed?        

        StorageClient storageClient;
        try {
            StorageFactoryClient sfc = new StorageFactoryClient(
                    storageFactoryEpr, SecurityHelper.getClientConfig());
            storageClient = sfc.createSMS();
            logger.info("Created SMS: <"
                    + storageClient.getEPR().getAddress().getStringValue()
                    + "> by user <"
                    + Session.getCurrent().getUser().getUsername() + ">");
        } catch (Exception e) {
            logger.error("Problem with creating new StorageClient instance!", e);
            return null;
        }
        return storageClient;
    }

    @Override
    public Collection<StorageEntity> getStorageEntities() throws UnavailableGridServiceException {
        List<StorageEntity> storageEntities = Lists.newArrayList();

        List<EndpointReferenceType> accessibleServices = getAccessibleServices(StorageFactory.SMF_PORT);
        for (EndpointReferenceType accessibleService : accessibleServices) {
            try {
                // FIXME: is there a way to get rid of client instance to get storage eprs
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
}
