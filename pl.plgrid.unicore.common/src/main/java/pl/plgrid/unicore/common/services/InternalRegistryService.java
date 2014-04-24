package pl.plgrid.unicore.common.services;

import de.fzj.unicore.wsrflite.xmlbeans.client.RegistryClient;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.PortalConfiguration;
import eu.unicore.portal.core.Session;
import org.apache.log4j.Logger;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;

/* 
 * TODO: handling registry unavailability
 */
class InternalRegistryService {

    private static final Logger logger = Logger.getLogger(InternalRegistryService.class);
    private static final String GRID_REGISTRY_URL = GlobalState
            .getCurrent()
            .getPortalConfiguration()
            .getProperties()
            .getProperty(
                    PortalConfiguration.CORE_PREFIX
                            + PortalConfiguration.REGISTRIES
            );

    private RegistryClient registryClient;

    InternalRegistryService() {
        initializeRegistryClient();
    }

    RegistryClient getRegistryClient() {
        return registryClient;
    }

    boolean reInitializeClient() {
        initializeRegistryClient();
        return getRegistryClient() == null;
    }

    private void initializeRegistryClient() {
        String username = Session.getCurrent().getUser().getUsername();
        String registryUrl = GRID_REGISTRY_URL;

        logger.info("Initializing registry object: <" + registryUrl + "> by user: <" + username + ">");
        EndpointReferenceType registryEpr = EndpointReferenceType.Factory
                .newInstance();
        registryEpr.addNewAddress().setStringValue(registryUrl);
        try {
            registryClient = new RegistryClient(registryEpr, SecurityHelper
                    .getClientConfig());
        } catch (Exception ex) {
            logger.error("Error accessing registry <" + registryUrl
                    + "> as user <" + username + ">", ex);
            registryClient = null;
        }
    }
}
