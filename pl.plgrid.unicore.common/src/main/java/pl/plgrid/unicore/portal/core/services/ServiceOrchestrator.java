package pl.plgrid.unicore.portal.core.services;

import org.chemomentum.common.ws.IServiceOrchestrator;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;

/**
 * Created by Rafal on 2014-05-26.
 */
public class ServiceOrchestrator extends AbstractService {
    private IServiceOrchestrator serviceOrchestratorClient;

    public ServiceOrchestrator() {
        try {
            initializeClient();
        } catch (UnavailableGridServiceException e) {
            logger.error("Could not initialize ServiceOrchestrator Client", e);
        }
    }

    public IServiceOrchestrator getSOClient() throws UnavailableGridServiceException {
        if (serviceOrchestratorClient == null) {
            initializeClient();
        }
        return serviceOrchestratorClient;
    }

    private void initializeClient() throws UnavailableGridServiceException {
        serviceOrchestratorClient = getServiceClient(IServiceOrchestrator.class,
                IServiceOrchestrator.PORT);
    }
}
