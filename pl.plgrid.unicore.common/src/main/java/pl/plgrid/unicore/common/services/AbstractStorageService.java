package pl.plgrid.unicore.common.services;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import de.fzj.unicore.uas.StorageManagement;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;


abstract class AbstractStorageService extends AbstractService {

    abstract public <T> List<T> createClients() throws UnavailableGridServiceException;


    protected Collection<StorageManagement> getStorageClients(QName port) throws UnavailableGridServiceException {
        List<StorageManagement> storageManagementList = Lists.newArrayList();
        List<EndpointReferenceType> accessibleServices = getAccessibleServices(port);
        for (EndpointReferenceType accessibleServiceEpr : accessibleServices) {
            try {
                StorageManagement client = initializeClient(StorageManagement.class, accessibleServiceEpr);
                storageManagementList.add(client);
            } catch (MalformedURLException e) {
                logger.warn("Error during StorageManagement client creation: " + accessibleServiceEpr.getAddress().getStringValue());
            }
        }
        return storageManagementList;
    }
}
