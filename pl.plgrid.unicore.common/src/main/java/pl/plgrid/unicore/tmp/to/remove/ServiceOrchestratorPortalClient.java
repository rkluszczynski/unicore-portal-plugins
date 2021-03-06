package pl.plgrid.unicore.tmp.to.remove;

import eu.unicore.portal.core.Session;
import org.chemomentum.common.ws.IServiceOrchestrator;
import org.chemomentum.workassignment.xmlbeans.SubmitWorkAssignmentRequestDocument;
import org.chemomentum.workassignment.xmlbeans.SubmitWorkAssignmentResponseDocument;
import org.chemomentum.workassignment.xmlbeans.WorkAssignmentType;
import org.chemomentum.workassignment.xmlbeans.WorkDocument.Work;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.JobDefinitionDocument;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.JobDefinitionType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;


/**
 * @author rkluszczynski
 */
public class ServiceOrchestratorPortalClient {

    private IServiceOrchestrator soClient;

    public ServiceOrchestratorPortalClient() {
        super();

        GridServicesExplorer gridServicesExplorer = Session.getCurrent().getServiceRegistry().getService(GridServicesExplorer.class);
        try {
            this.soClient = gridServicesExplorer.getServiceOrchestratorService().createClient();
        } catch (UnavailableGridServiceException e) {
            e.printStackTrace();
        }
    }

    // ???:
    //public ArrayList<String> getApplications();
    // ???:
    //public ArrayList<String> getApplicationVersions(String applicationName);
    // ???:
    //public ArrayList<ResourceProperties> getUserGridResources();
    public String submitWorkAssignment(
            JobDefinitionDocument jobDefinitionDocument,
            String workAssignmentID, EndpointReferenceType waStorageEPR) {
        SubmitWorkAssignmentRequestDocument waDoc = SubmitWorkAssignmentRequestDocument.Factory
                .newInstance();
        WorkAssignmentType workAssignment = waDoc
                .addNewSubmitWorkAssignmentRequest().addNewWorkAssignment();

        Work work = workAssignment.addNewWork();
        JobDefinitionType jobDef = jobDefinitionDocument.getJobDefinition();
        work.setJobDefinition(jobDef);

        workAssignment.setParent(JobDefinitionUtil.VASP_GRID_JOBNAME);
        workAssignment.setId(workAssignmentID);

        if (waStorageEPR != null) {
            workAssignment.setStorageEPR(waStorageEPR);
        }

        /*
         * if(!isJSDL && builder.getImports().size()>0){
         * createWorkflowDataStorage(); wa.setStorageEPR(storageEPR); try{
         * uploadLocalData(builder, waID); }catch(IOException ex){
         * error("Can't upload local files.",ex); endProcessing(1); } }
         */
        SubmitWorkAssignmentResponseDocument response = this.soClient
                .submitWorkAssignment(waDoc);
        return response.toString();
    }

}
