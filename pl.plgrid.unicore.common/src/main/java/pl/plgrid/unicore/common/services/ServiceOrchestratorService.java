package pl.plgrid.unicore.common.services;


import org.chemomentum.common.ws.IServiceOrchestrator;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;


public class ServiceOrchestratorService extends AbstractService {
    @Override
    public IServiceOrchestrator createClient() throws UnavailableGridServiceException {
        return getServiceClient(IServiceOrchestrator.class,
                IServiceOrchestrator.PORT);
    }
}
