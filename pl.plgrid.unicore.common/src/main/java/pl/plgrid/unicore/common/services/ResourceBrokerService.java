package pl.plgrid.unicore.common.services;


import org.chemomentum.common.ws.IResourceBroker;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;


public class ResourceBrokerService extends AbstractService {
    public IResourceBroker createClient() throws UnavailableGridServiceException {
        return getServiceClient(IResourceBroker.class,
                IResourceBroker.PORT);
    }
}
