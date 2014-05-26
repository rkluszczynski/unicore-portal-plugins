package pl.plgrid.unicore.portal.core.entities;

import de.fzj.unicore.uas.client.TSSClient;
import org.apache.log4j.Logger;
import org.unigrids.x2006.x04.services.tss.TargetSystemPropertiesDocument.TargetSystemProperties;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableJobServiceException;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;


public class TSSClientEntity {
    protected static final Logger logger = Logger.getLogger(TSSClientEntity.class);

    private final EndpointReferenceType tssClientEpr;

    private TSSClient tssClient;
    private TargetSystemProperties tssProperties;


    public TSSClientEntity(EndpointReferenceType tssClientEpr) {
        this.tssClientEpr = tssClientEpr;
    }

    public EndpointReferenceType getTssClientEpr() {
        return tssClientEpr;
    }

    public TSSClient getTSSClient() {
        if (tssClient == null) {
            synchronized (tssClientEpr) {
                if (tssClient == null) {
                    try {
                        createClient();
                    } catch (Exception e) {
                        logger.error("Error during job client creation: " + tssClientEpr, e);
                    }
                }
            }
        }
        return tssClient;
    }

    public TargetSystemProperties getTssProperties() {
        if (tssProperties == null) {
            synchronized (tssClientEpr) {
                if (tssProperties == null) {
                    try {
                        createClient();
                    } catch (Exception e) {
                        logger.error("Error during job client creation: " + tssClientEpr, e);
                    }
                }
            }
        }
        return tssProperties;
    }

    @Override
    public String toString() {
        String propertiesString = "";
        if (logger.isTraceEnabled()) {
            propertiesString = String.format(", tssProperties=%s", tssProperties);
        }
        return String.format("TSSClientEntity{tssClientEpr=%s%s}",
                tssClientEpr.getAddress().getStringValue(),
                propertiesString);
    }


    private void createClient() throws UnavailableJobServiceException {
        try {
            tssClient = new TSSClient(tssClientEpr, SecurityHelper.getClientConfig());
            tssProperties = tssClient
                    .getResourcePropertiesDocument()
                    .getTargetSystemProperties();
        } catch (Exception e) {
            throw new UnavailableJobServiceException("Unable to create tss client: " + tssClientEpr, e);
        }
    }
}
