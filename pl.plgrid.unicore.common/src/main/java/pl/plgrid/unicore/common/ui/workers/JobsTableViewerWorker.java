package pl.plgrid.unicore.common.ui.workers;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import de.fzj.unicore.uas.client.JobClient;
import de.fzj.unicore.uas.client.TSSClient;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.threads.BackgroundWorker;
import eu.unicore.portal.core.threads.IProgressMonitor;
import org.apache.log4j.Logger;
import org.unigrids.x2006.x04.services.jms.JobPropertiesDocument;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;
import pl.plgrid.unicore.common.services.TargetSystemService;
import pl.plgrid.unicore.common.ui.SimulationsTableViewer;
import pl.plgrid.unicore.common.ui.model.AtomicJobViewerData;
import pl.plgrid.unicore.common.ui.model.SimulationViewerData;
import pl.plgrid.unicore.common.utils.SecurityHelper;

import java.util.List;


/*
TODO: clean this up!!
*/
public class JobsTableViewerWorker extends BackgroundWorker {
    private static final Logger logger = Logger.getLogger(SimulationsTableViewer.class);

    private final TargetSystemService targetSystemService;

    private final List<SimulationViewerData> dataList = Lists.newArrayList();
    private final Table table;

    public JobsTableViewerWorker(Table table) {
        super(GlobalState.getMessage(CommonComponentsI18N.ID, "simulationsTableViewer.jobs.getList"));
        this.table = table;

        GridServicesExplorer gridExplorer
                = Session.getCurrent().getServiceRegistry().getService(GridServicesExplorer.class);
        targetSystemService = gridExplorer.getTargetSystemService();
    }

    @Override
    protected void work(IProgressMonitor progress) {
        try {
            prepareSimulationTableData();
        } catch (UnavailableGridServiceException ex) {
            String message = "Problem with filling jobs table of user <" +
                    Session.getCurrent().getUser().getUsername() + ">";
            logger.error(message, ex);
            Notification.show(message, ex.getMessage(),
                    Notification.Type.WARNING_MESSAGE);
        }
    }

    @Override
    protected void updateUI() {
        super.updateUI();
        getTable().removeAllItems();

        for (SimulationViewerData simulationViewerData : dataList) {
            getTable().addItem(
                    new Object[]{
                            simulationViewerData.getStatus(),
                            simulationViewerData.getSimulationName(),
                            simulationViewerData.getSubmissionDate()
                    },
                    simulationViewerData
            );
        }
        getTable().markAsDirtyRecursive();
    }


    private Table getTable() {
        return table;
    }

    private void prepareSimulationTableData() throws UnavailableGridServiceException {
        for (TSSClient client : targetSystemService.createClient()) {
            List<EndpointReferenceType> jobs = Lists.newArrayList();
            try {
                jobs.addAll(client.getJobs());
            } catch (Exception e) {
                logger.warn("Problem getting jobs of TSS: " + client.getEPR(), e);
                e.printStackTrace();
            }

            for (EndpointReferenceType endpointReferenceType : jobs) {
                JobPropertiesDocument.JobProperties jobProperties = null;
                try {
                    // TODO: optimize this overkilling status asking one by one

                    JobClient jobClient = new JobClient(endpointReferenceType, SecurityHelper.getClientConfig());

                    jobProperties = jobClient
                            .getResourcePropertiesDocument()
                            .getJobProperties();

                } catch (Exception e) {
                    logger.warn("Problem getting job's properties: " + endpointReferenceType, e);
                    e.printStackTrace();
                }

                // FIXME: use it in future
//                jobsStatusMap = client.getJobsStatus(clientJobsStringList);
                dataList.add(
                        convertJobPropertiesToAtomicJobViewerData(
                                endpointReferenceType,
                                jobProperties
                        )
                );
            }
        }
    }

    private SimulationViewerData convertJobPropertiesToAtomicJobViewerData(
            EndpointReferenceType jobEpr,
            JobPropertiesDocument.JobProperties jobProperties
    ) {
        AtomicJobViewerData atomicJobViewerData = new AtomicJobViewerData(jobEpr);
        if (jobProperties != null) {
            atomicJobViewerData.setSimulationName(
                    jobProperties
                            .getOriginalJSDL()
                            .getJobDescription()
                            .getJobIdentification()
                            .getJobName()
            );
            atomicJobViewerData.setDirectoryEpr(
                    jobProperties
                            .getWorkingDirectoryReference()
            );
            atomicJobViewerData.setStatus(
                    jobProperties
                            .getStatusInfo()
                            .getStatus()
                            .toString()
            );
            atomicJobViewerData.setSubmissionDate(
                    jobProperties
                            .getSubmissionTime()
                            .getTime()
            );
        }
        return atomicJobViewerData;
    }
}
