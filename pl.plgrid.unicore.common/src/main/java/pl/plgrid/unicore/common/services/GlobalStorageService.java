package pl.plgrid.unicore.common.services;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import de.fzj.unicore.uas.StorageManagement;
import de.fzj.unicore.uas.client.StorageClient;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.common.utils.SecurityHelper;

import java.util.Collections;
import java.util.List;

public class GlobalStorageService extends AbstractStorageService {

    @Override
    public StorageClient createClient() throws UnavailableGridServiceException {
        List<StorageClient> clients = getClients(true);
        if (clients.isEmpty()) {
            throw new UnavailableGridServiceException("There is no StorageClient");
        }
        return clients.get(0);
    }

    @Override
    public List<StorageClient> createClients() throws UnavailableGridServiceException {
        return getClients(false);
    }

    private List<StorageClient> getClients(boolean getOne) throws UnavailableGridServiceException {
        List<StorageClient> globalStorageClients = Lists.newArrayList();
        List<EndpointReferenceType> accessibleServices = getAccessibleServices(StorageManagement.SMS_PORT);
        Collections.shuffle(accessibleServices);
        for (EndpointReferenceType accessibleServiceEpr : accessibleServices) {
            try {
                StorageClient storageClient = new StorageClient(accessibleServiceEpr,
                        SecurityHelper.getClientConfig());
                globalStorageClients.add(storageClient);
                if (getOne) {
                    return globalStorageClients;
                }
            } catch (Exception e) {
                logger.warn("Problem with creation client for: " + accessibleServiceEpr, e);
            }
        }
        return globalStorageClients;
    }
}
