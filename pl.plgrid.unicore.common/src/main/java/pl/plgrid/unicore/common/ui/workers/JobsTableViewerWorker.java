package pl.plgrid.unicore.common.ui.workers;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import de.fzj.unicore.uas.client.JobClient;
import de.fzj.unicore.uas.client.TSSClient;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.threads.BackgroundWorker;
import eu.unicore.portal.core.threads.IProgressMonitor;
import org.apache.log4j.Logger;
import org.unigrids.services.atomic.types.StatusType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.common.i18n.CommonComponentsI18N;
import pl.plgrid.unicore.common.services.TargetSystemService;
import pl.plgrid.unicore.common.ui.JobsTableViewer;
import pl.plgrid.unicore.common.utils.SecurityHelper;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/*
TODO: clean this up!!
*/
public class JobsTableViewerWorker extends BackgroundWorker {
    private static final Logger logger = Logger.getLogger(JobsTableViewer.class);

    public static final String ITEM_ID_JOIN_STRING = "~";

    private final TargetSystemService targetSystemService;
    private final Collection<JobClient> jobsClients = Lists.newArrayList();
    private final Map<String, StatusType.Enum> jobsStatusMap = Maps.newHashMap();
    private final Table table;

    public JobsTableViewerWorker(Table table, TargetSystemService targetSystemService) {
        super(GlobalState.getMessage(CommonComponentsI18N.ID, "jobsTableViewer.jobs.getList"));
        this.table = table;
        this.targetSystemService = targetSystemService;
    }

    @Override
    protected void work(IProgressMonitor progress) {
        try {
            populateJobsTable();
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
        for (JobClient jc : jobsClients) {
            String jobName;
            String dirURI;
            try {
                jobName = jc.getResourcePropertiesDocument()
                        .getJobProperties().getOriginalJSDL()
                        .getJobDescription().getJobIdentification()
                        .getJobName();
                dirURI = jc.getResourcePropertiesDocument()
                        .getJobProperties()
                        .getWorkingDirectoryReference()
                        .getAddress()
                        .getStringValue();
            } catch (Exception ex) {
                logger.warn("Problem getting job's name", ex);
                jobName = jc.getFriendlyName();
                dirURI = " - error - ";
            }

            String jobURI = jc.getEPR().getAddress().getStringValue();
            StatusType.Enum jobStatus = jobsStatusMap.get(jobURI.split("=")[1]);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            String submissionDateString = simpleDateFormat.format(
                    jc.getSubmissionTime().getTime()
            );


//            PortalApplication.getCurrent().asyncGUIUpdate();

            getTable().addItem(
                    new Object[]{
                            (jobStatus == null) ? "( unknown )" : jobStatus.toString(),
                            jobName,
                            submissionDateString
                    },
                    String.format("%s%s%s", jobURI, ITEM_ID_JOIN_STRING, dirURI)
            );
        }
        getTable().markAsDirtyRecursive();
    }


    private Table getTable() {
        return table;
    }

    private void populateJobsTable() throws UnavailableGridServiceException {
        for (TSSClient client : targetSystemService.createClient()) {
            final List<String> clientJobsStringList = Lists.newArrayList();
            try {
                Collection<JobClient> tssJobsClients = Collections2.transform(client.getJobs(), new Function<EndpointReferenceType, JobClient>() {
                    @Override
                    public JobClient apply(EndpointReferenceType epr) {
                        try {
                            clientJobsStringList.add(epr
                                    .getAddress()
                                    .getStringValue()
                                    .split("=")[1]);
                            JobClient jc = new JobClient(epr, SecurityHelper.getClientConfig());

                            // TODO: optimize this overkilling status asking one by one
                            jobsStatusMap.put(
                                    epr
                                            .getAddress()
                                            .getStringValue()
                                            .split("=")[1],
                                    jc
                                            .getStatus()
                            );

                            return jc;
                        } catch (Exception ex) {
                            logger.error("Problem creating job client for EPR", ex);
                            return null;
                        }
                    }
                });
                jobsClients.addAll(Collections2.filter(tssJobsClients, new Predicate<JobClient>() {
                    @Override
                    public boolean apply(JobClient t) {
                        return t != null;
                    }
                }));

                // FIXME: use it in future
//                jobsStatusMap = client.getJobsStatus(clientJobsStringList);
            } catch (Exception ex) {
                logger.error("Problem with getting jobs info for table", ex);
            }
        }
    }
}
