package pl.plgrid.unicore.vasp.session;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import de.fzj.unicore.uas.client.EnumerationClient;
import de.fzj.unicore.uas.client.JobClient;
import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.uas.client.TSSClient;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.threads.BackgroundWorker;
import eu.unicore.portal.core.threads.IProgressMonitor;
import org.apache.log4j.Logger;
import org.unigrids.services.atomic.types.AvailableResourceType;
import org.unigrids.services.atomic.types.AvailableResourceTypeType.Enum;
import org.unigrids.services.atomic.types.ProtocolType;
import org.unigrids.x2006.x04.services.tss.JobReferenceDocument;
import org.unigrids.x2006.x04.services.tss.TargetSystemPropertiesDocument;
import pl.plgrid.unicore.common.utils.SecurityHelper;
import pl.plgrid.unicore.vasp.client.ServiceOrchestratorPortalClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author rkluszczynski
 */
public class UserSessionGridState {

    private static final Logger logger = Logger
            .getLogger(UserSessionGridState.class);

    private final ServiceClientsFactory serviceClientsFactory;
    private final ServiceOrchestratorPortalClient soPortalClient;

    public UserSessionGridState() {
        super();
        this.serviceClientsFactory = new ServiceClientsFactory();
        this.soPortalClient = new ServiceOrchestratorPortalClient(
                serviceClientsFactory.getServiceOrchestratorClient());
    }

    public ServiceClientsFactory getServiceClientsFactory() {
        return serviceClientsFactory;
    }

    public ServiceOrchestratorPortalClient getSoPortalClient() {
        return soPortalClient;
    }

    public String importFileToGrid(StorageClient storageClient, String filename, String content,
                                   String workAssignmentID) throws Exception {
        InputStream source = new ByteArrayInputStream(content.getBytes());
        storageClient.getImport(filename, ProtocolType.BFT,
                ProtocolType.RBYTEIO).writeAllData(source);
        return ProtocolType.BFT + ":"
                + storageClient.getEPR().getAddress().getStringValue()
                + "#" + filename;
    }

    public void gatherAllAvailableResources() {
        gatherAllAvailableResources(null);
    }

    public interface UIResourcesUpdater {

        public void updateUI(
                ConcurrentMap<String, ConcurrentHashMap<Enum, ArrayList<AvailableResourceType>>> m,
                boolean isFinished);
    }

    public void gatherAllAvailableResources(
            final UIResourcesUpdater uiResourcesUpdater) {
        List<TSSClient> tssList = null;
        try {
            tssList = serviceClientsFactory.getTSSClients();
        } catch (Exception e1) {
            logger.error("Error getting TSS clients for user >"
                    + Session.getCurrent().getUser().getUsername() + ">", e1);
            return;
        }

        final ConcurrentMap<String, ConcurrentHashMap<Enum, ArrayList<AvailableResourceType>>> m = new ConcurrentHashMap<String, ConcurrentHashMap<org.unigrids.services.atomic.types.AvailableResourceTypeType.Enum, ArrayList<AvailableResourceType>>>();
        final CountDownLatch fetchedLatch = new CountDownLatch(tssList.size());
        for (final TSSClient tss : tssList) {
            new BackgroundWorker("Getting resources from TSS "
                    + tss.getEPR().getAddress().getStringValue()) {
                @Override
                protected void work(IProgressMonitor progress) {
                    try {
                        TargetSystemPropertiesDocument resourcePropertiesDocument = tss.getResourcePropertiesDocument();
                        logger.debug(resourcePropertiesDocument);

                        for (AvailableResourceType art : tss
                                .getAvailableResources()) {
                            m.putIfAbsent(
                                    art.getName(),
                                    new ConcurrentHashMap<org.unigrids.services.atomic.types.AvailableResourceTypeType.Enum, ArrayList<AvailableResourceType>>());
                            m.get(art.getName()).putIfAbsent(art.getType(),
                                    new ArrayList<AvailableResourceType>());
                            m.get(art.getName()).get(art.getType()).add(art);
                        }
                    } catch (Exception e) {
                        logger.error(
                                "Problem with getting available resources from TSS <"
                                        + tss.getFriendlyName()
                                        + "> by user <"
                                        + Session.getCurrent().getUser()
                                        .getUsername() + ">", e
                        );
                    }
                    fetchedLatch.countDown();
                }

                @Override
                protected void updateUI() {
                    super.updateUI();
                    if (uiResourcesUpdater != null) {
                        uiResourcesUpdater.updateUI(m, false);
                    }
                }
            }.schedule();
        }

        new BackgroundWorker("Gathering all resources") {
            @Override
            protected void work(IProgressMonitor progress) {
                try {
                    fetchedLatch.await(60L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    logger.error("Interrupted: " + this.getName(), e);
                }
            }

            @Override
            protected void updateUI() {
                super.updateUI();
                if (uiResourcesUpdater != null) {
                    uiResourcesUpdater.updateUI(m, true);
                }
            }
        }.schedule();
    }

    public void getJobs() {
        getJobs(null);
    }

    public interface UIJobsUpdater {

        public void updateUI(List<JobClient> jobs, boolean isFinished);
    }

    public void getJobs(final UIJobsUpdater uiJobsUpdater) {
        List<EnumerationClient<JobReferenceDocument>> enumClientlist = null;
        try {
            enumClientlist = Lists
                    .transform(
                            serviceClientsFactory.getTSSClients(),
                            new Function<TSSClient, EnumerationClient<JobReferenceDocument>>() {

                                @Override
                                public EnumerationClient<JobReferenceDocument> apply(
                                        TSSClient tssClient) {
                                    try {
                                        return tssClient
                                                .getJobReferenceEnumeration();
                                    } catch (Exception e) {
                                        logger.error(
                                                "Problem getting enumeration client from TSS <"
                                                        + tssClient
                                                        .getFriendlyName()
                                                        + "> by user <"
                                                        + Session.getCurrent()
                                                        .getUser()
                                                        .getUsername()
                                                        + ">", e
                                        );
                                        return null;
                                    }
                                }
                            }
                    );
        } catch (Exception e) {
            logger.error("Problem during getting TSS clients", e);
            return;
        }

        final CountDownLatch fetchedLatch = new CountDownLatch(
                enumClientlist.size());
        final List<JobClient> jobs = Lists.newArrayList();
        for (final EnumerationClient<JobReferenceDocument> enumClient : enumClientlist) {
            new BackgroundWorker("Getting jobs from "
                    + enumClient.getFriendlyName()) {

                @Override
                protected void work(IProgressMonitor progress) {
                    for (JobReferenceDocument jrd : enumClient) {
                        try {
                            jobs.add(new JobClient(jrd.getJobReference(),
                                    SecurityHelper.getClientConfig()));
                        } catch (Exception e) {
                            logger.warn("Problem with job reference <"
                                    + jrd.getJobReference().getAddress()
                                    .getStringValue()
                                    + "> during access by user <"
                                    + Session.getCurrent().getUser()
                                    .getUsername() + ">", e);
                        }
                    }
                    fetchedLatch.countDown();
                }

                @Override
                protected void updateUI() {
                    super.updateUI();
                    if (uiJobsUpdater != null) {
                        uiJobsUpdater.updateUI(jobs, false);
                    }
                }
            }.schedule();
        }

        new BackgroundWorker("Gathering all jobs") {

            @Override
            protected void work(IProgressMonitor progress) {
                try {
                    fetchedLatch.await(60L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    logger.error("Interrupted: " + this.getName(), e);
                }
            }

            @Override
            protected void updateUI() {
                super.updateUI();
                if (uiJobsUpdater != null) {
                    uiJobsUpdater.updateUI(jobs, true);
                }
            }
        }.schedule();
    }
}
