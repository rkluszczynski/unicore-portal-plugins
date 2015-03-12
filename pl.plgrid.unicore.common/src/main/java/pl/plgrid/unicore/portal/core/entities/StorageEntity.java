package pl.plgrid.unicore.portal.core.entities;

import de.fzj.unicore.uas.client.StorageClient;
import org.apache.log4j.Logger;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableStorageServiceException;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;

import static org.unigrids.x2006.x04.services.sms.StoragePropertiesDocument.StorageProperties;

public class StorageEntity {
    protected static final Logger logger = Logger.getLogger(StorageEntity.class);

    private final EndpointReferenceType storageEpr;
    private final StorageEntityType storageEntityType;

    private StorageClient storageClient;
    private StorageProperties storageProperties;


    public StorageEntity(EndpointReferenceType storageEpr, StorageEntityType storageEntityType) {
        this.storageEpr = storageEpr;
        this.storageEntityType = storageEntityType;
    }

    public StorageClient getStorageClient() {
        if (storageClient == null) {
            synchronized (storageEpr) {
                if (storageClient == null) {
                    try {
                        createClient();
                    } catch (Exception e) {
                        logger.error("Unable to create storage client: " + storageEpr, e);
                    }
                }
            }
        }
        return storageClient;
    }

    public StorageProperties getStorageProperties() {
        return storageProperties;
    }

    private void createClient() throws UnavailableStorageServiceException {
        try {
            storageClient = new StorageClient(storageEpr, SecurityHelper.getClientConfig());
            storageProperties = storageClient
                    .getResourcePropertiesDocument()
                    .getStorageProperties();
        } catch (Exception e) {
            throw new UnavailableStorageServiceException("Unable to create storage client: " + storageClient, e);
        }
    }

    @Override
    public String toString() {
        String propertiesString = "";
        if (logger.isTraceEnabled()) {
            propertiesString = String.format(", storageProperties=%s", storageProperties);
        }
        return String.format("StorageEntity{storageEpr=%s, storageEntityType=%s%s}",
                storageEpr.getAddress().getStringValue(),
                storageEntityType, propertiesString);
    }
}
