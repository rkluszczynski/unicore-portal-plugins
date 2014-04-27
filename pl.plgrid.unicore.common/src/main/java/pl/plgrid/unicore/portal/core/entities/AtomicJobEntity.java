package pl.plgrid.unicore.portal.core.entities;

import de.fzj.unicore.uas.client.JobClient;
import org.apache.log4j.Logger;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableJobServiceException;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;

import static org.unigrids.x2006.x04.services.jms.JobPropertiesDocument.JobProperties;


public class AtomicJobEntity {
    protected static final Logger logger = Logger.getLogger(AtomicJobEntity.class);

    private final EndpointReferenceType jobEpr;

    private JobClient jobClient;
    private JobProperties jobProperties;


    public AtomicJobEntity(EndpointReferenceType jobEpr) {
        this.jobEpr = jobEpr;
    }

    public JobClient getJobClient() {
        if (jobClient == null) {
            synchronized (jobEpr) {
                if (jobClient == null) {
                    try {
                        createClient();
                    } catch (Exception e) {
                        logger.error("Error during job client creation: " + jobEpr, e);
                    }
                }
            }
        }
        return jobClient;
    }

    public JobProperties getJobProperties() {
        return jobProperties;
    }

    @Override
    public String toString() {
        String propertiesString = "";
        if (logger.isTraceEnabled()) {
            propertiesString = String.format(", jobProperties=%s", jobProperties);
        }
        return String.format("AtomicJobEntity{jobEpr=%s%s}",
                jobEpr.getAddress().getStringValue(),
                propertiesString);
    }


    private void createClient() throws UnavailableJobServiceException {
        try {
            jobClient = new JobClient(jobEpr, SecurityHelper.getClientConfig());
            jobProperties = jobClient
                    .getResourcePropertiesDocument()
                    .getJobProperties();
        } catch (Exception e) {
            throw new UnavailableJobServiceException("Unable to create job client: " + jobEpr, e);
        }
    }
}
