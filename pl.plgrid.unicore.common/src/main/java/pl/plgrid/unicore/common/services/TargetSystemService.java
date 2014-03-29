package pl.plgrid.unicore.common.services;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import de.fzj.unicore.uas.TargetSystemFactory;
import de.fzj.unicore.uas.client.TSFClient;
import de.fzj.unicore.uas.client.TSSClient;
import org.unigrids.x2006.x04.services.tsf.CreateTSRDocument;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;


public class TargetSystemService extends AbstractService {

    public List<TSSClient> createClient() throws UnavailableGridServiceException {
        return getServiceClients(TargetSystemFactory.TSF_PORT);
    }

    private List<TSSClient> getServiceClients(QName port) throws UnavailableGridServiceException {
        List<EndpointReferenceType> accessibleServices = getAccessibleServices(port);
        ArrayList<TSSClient> tssClients = Lists.newArrayList();

        for (EndpointReferenceType accessibleService : accessibleServices) {
            TSFClient tsfClient = null;
            try {
                tsfClient = new TSFClient(accessibleService, getClientConfig());
                TSSClient tssClient = null;
                if (tsfClient.getAccessibleTargetSystems().isEmpty()) {
                    CreateTSRDocument in = CreateTSRDocument.Factory.newInstance();
                    in.addNewCreateTSR();
                    tssClient = tsfClient.createTSS(in);
                }

                if (tssClient == null) {
                    // TODO: handle somehow admin privileges
                    for (EndpointReferenceType tssEpr : tsfClient
                            .getAccessibleTargetSystems()) {
                        tssClients.add(new TSSClient(tssEpr, getClientConfig()));
                        logger.info("Added user's TSS epr: " + tssEpr.getAddress().getStringValue());
                        break;
                    }
                }
                logger.info("Processed TSF epr: " + accessibleService.getAddress().getStringValue());
            } catch (Exception ex) {
                String message = ((tsfClient == null)
                        ? "Problem with getting TSF client for EPR: "
                        : "Problem with creating TSS client for TSF: ")
                        + accessibleService.getAddress().getStringValue();
                logger.warn(message, ex);
            }
        }

        if (tssClients.isEmpty()) {
            throw new UnavailableGridServiceException("No target system services for user");
        }
        return tssClients;
    }
}