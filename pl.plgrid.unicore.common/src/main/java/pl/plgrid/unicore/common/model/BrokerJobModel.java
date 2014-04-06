package pl.plgrid.unicore.common.model;

import com.vaadin.ui.Notification;
import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import org.apache.log4j.Logger;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.JobDefinitionDocument;
import pl.plgrid.unicore.tmp.to.remove.ServiceOrchestratorPortalClient;

public class BrokerJobModel extends AbstractJobModel {
    private static final Logger logger = Logger.getLogger(BrokerJobModel.class);

    private static final String VASP_GRID_JOBNAME = "VASP_Job_submitted_by_Portal";

    public BrokerJobModel(String applicationName, String applicationVersion) {
        super(applicationName, applicationVersion);
        workAssignmentID = WSUtilities.newUniqueID();
    }

    @Override
    public void submit() {
        JobDefinitionDocument jobDefinitionDocument =
                prepareJobDefinitionDocument(VASP_GRID_JOBNAME);

        logger.info("BROKER JOB: " + jobDefinitionDocument.toString());

        String msg = new ServiceOrchestratorPortalClient().submitWorkAssignment(
                jobDefinitionDocument, workAssignmentID, storageClient.getEPR());

        // TODO: handle missing application or version in SO

        Notification.show("Submitting VASP job...", msg,
                Notification.Type.TRAY_NOTIFICATION);

    }
}
