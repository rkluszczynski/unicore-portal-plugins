package pl.plgrid.unicore.common.services;


import org.chemomentum.common.ws.IServiceOrchestrator;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;


public class ServiceOrchestratorService extends AbstractService {
    public IServiceOrchestrator createClient() throws UnavailableGridServiceException {
        return getServiceClient(IServiceOrchestrator.class,
                IServiceOrchestrator.PORT);
    }
}
