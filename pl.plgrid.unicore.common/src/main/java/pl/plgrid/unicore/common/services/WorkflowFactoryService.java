package pl.plgrid.unicore.common.services;


import org.chemomentum.common.impl.workflow.WorkflowSubmissionClient;
import org.chemomentum.common.ws.IWorkflowFactory;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;

import java.util.Collections;
import java.util.List;


public class WorkflowFactoryService extends AbstractService {
    @Override
    public WorkflowSubmissionClient createClient() throws UnavailableGridServiceException {
        List<EndpointReferenceType> accessibleServices = getAccessibleServices(IWorkflowFactory.PORTTYPE);
        if (accessibleServices.size() > 1) {
            Collections.shuffle(accessibleServices);
        }
        for (EndpointReferenceType accessibleServiceEpr : accessibleServices) {
            try {
                WorkflowSubmissionClient client = new WorkflowSubmissionClient(accessibleServiceEpr,
                        SecurityHelper.getClientConfig());
                return client;
            } catch (Exception e) {
                logger.error("Unable to create submission workflow client: " + accessibleServiceEpr, e);
                e.printStackTrace();
            }
        }
        return null;
    }
}
