package pl.plgrid.unicore.tmp.to.remove;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import eu.unicore.jsdl.extensions.IgnoreFailureDocument;
import org.chemomentum.broker.xmlbeans.ChooseResourceRequestDocument;
import org.chemomentum.broker.xmlbeans.ChooseResourceRequestDocument.ChooseResourceRequest;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.*;

import java.util.List;

/**
 * @author rkluszczynski
 */
public class JobDefinitionUtil {

    private static final String VASP_APPLICATON_NAME = "VASP";
    private static final String VASP_APPLICATION_VERSION = "5.2";

    public static final String VASP_GRID_JOBNAME = "VASP_Job_submitted_by_Portal";


    static private JobDefinitionDocument simpleDateJobDocument() {
        JobDescriptionType jobDesc = JobDescriptionType.Factory.newInstance();

        ApplicationDocument appDoc = ApplicationDocument.Factory.newInstance();
        ApplicationType app = appDoc.addNewApplication();
        app.setApplicationName("Date");
        //app.setApplicationVersion("");
        jobDesc.setApplication(app);

        jobDesc.addNewJobIdentification().setJobName("Simple-Date-Job");

        JobDefinitionDocument jobDefinitionDocument = JobDefinitionDocument.Factory.newInstance();
        jobDefinitionDocument.addNewJobDefinition().setJobDescription(jobDesc);

        return jobDefinitionDocument;
    }

    public static ChooseResourceRequestDocument getChooseResourceRequestDocument() {
        ChooseResourceRequestDocument reqdoc = ChooseResourceRequestDocument.Factory.newInstance();
        ChooseResourceRequest req = reqdoc.addNewChooseResourceRequest();
        JobDefinitionType jdt = req.addNewJobDefinition();
        jdt.setJobDescription(simpleDateJobDocument().getJobDefinition().getJobDescription());
        return reqdoc;
    }

    public static JobDefinitionDocument simpleDateJobDocument(
            List<String> asList) {
        JobDescriptionType jobDesc = JobDescriptionType.Factory.newInstance();

        ApplicationDocument appDoc = ApplicationDocument.Factory.newInstance();
        ApplicationType app = appDoc.addNewApplication();
        app.setApplicationName("Date");
        jobDesc.setApplication(app);

        jobDesc.addNewJobIdentification().setJobName("Simple-Date-Job-With-Import");

        List<DataStagingType> staging = Lists.newArrayList();
        for (String uri : asList) {
            DataStagingDocument dsd = DataStagingDocument.Factory.newInstance();
            DataStagingType d = dsd.addNewDataStaging();

            d.addNewSource().setURI(uri);
            d.setFileName(uri.substring(uri.lastIndexOf("#") + 1));

            d.setCreationFlag(CreationFlagEnumeration.OVERWRITE);

            IgnoreFailureDocument ifd = IgnoreFailureDocument.Factory.newInstance();
            ifd.setIgnoreFailure(true);

            WSUtilities.append(ifd, dsd);
            staging.add(d);
        }
        jobDesc.setDataStagingArray(staging.toArray(new DataStagingType[staging.size()]));

        JobDefinitionDocument jobDefinitionDocument = JobDefinitionDocument.Factory.newInstance();
        jobDefinitionDocument.addNewJobDefinition().setJobDescription(jobDesc);

        return jobDefinitionDocument;
    }

    public static JobDefinitionDocument createVASPJobDocument(List<String> jobImports) {
        JobDescriptionType jobDesc = JobDescriptionType.Factory.newInstance();
        ApplicationDocument appDoc = ApplicationDocument.Factory.newInstance();
        ApplicationType app = appDoc.addNewApplication();
        app.setApplicationName(VASP_APPLICATON_NAME);
        app.setApplicationVersion(VASP_APPLICATION_VERSION);
        jobDesc.setApplication(app);

        jobDesc.addNewJobIdentification().setJobName(VASP_GRID_JOBNAME);

        List<DataStagingType> staging = Lists.newArrayList();
        for (String uri : jobImports) {
            DataStagingDocument dsd = DataStagingDocument.Factory.newInstance();
            DataStagingType d = dsd.addNewDataStaging();

            d.addNewSource().setURI(uri);
            d.setFileName(uri.substring(uri.lastIndexOf("#") + 1));

            d.setCreationFlag(CreationFlagEnumeration.OVERWRITE);

            IgnoreFailureDocument ifd = IgnoreFailureDocument.Factory.newInstance();
            ifd.setIgnoreFailure(false);

            WSUtilities.append(ifd, dsd);
            staging.add(d);
        }
        jobDesc.setDataStagingArray(staging.toArray(new DataStagingType[staging.size()]));

        JobDefinitionDocument jobDefinitionDocument = JobDefinitionDocument.Factory.newInstance();
        jobDefinitionDocument.addNewJobDefinition().setJobDescription(jobDesc);

        return jobDefinitionDocument;
    }

    private void makeJob() throws Exception {
        // FIXME to delete
        JobDescriptionType jd = JobDescriptionType.Factory.newInstance();

        ApplicationDocument ad = ApplicationDocument.Factory.newInstance();
        ApplicationType app = ad.addNewApplication();
        app.setApplicationName("Date");
        //app.setApplicationVersion("");
        jd.setApplication(app);

        jd.addNewJobIdentification().setJobName("Example-Grid-Job");

        /*
         String jobProject=getProperty("Project");
         if(jobProject!=null){
         jd.getJobIdentification().addJobProject(jobProject);
         }

         String jobDescription=getProperty("Description");
         if(jobDescription!=null){
         if(jd.getJobIdentification()==null)jd.addNewJobIdentification();
         jd.getJobIdentification().setDescription(jobDescription);
         }
         String email=getProperty("User email");
         if(email!=null){
         if(jd.getJobIdentification()==null)jd.addNewJobIdentification();
         jd.getJobIdentification().addJobAnnotation("User email: "+email);
         }

         //"not before" for scheduling server-side processing
         try{
         String notBefore=json.getString("Not before");
         if(jd.getJobIdentification()==null)jd.addNewJobIdentification();
         jd.getJobIdentification().addJobAnnotation("notBefore: "+notBefore);
         }catch(Exception e){}

         //other annotations
         try{
         JSONArray a=json.getJSONArray("Tags");
         if(jd.getJobIdentification()==null)jd.addNewJobIdentification();
         for(int i=0; i<a.length();i++){
         jd.getJobIdentification().addJobAnnotation(a.getString(i));
         }
         }catch(Exception e){}

         try{
         jd.setResources(makeResources((JSONObject)json.get("Resources")));
         }catch(Exception e){}
		
		
         List<DataStagingType>staging=new ArrayList<DataStagingType>();
         addStageIn(staging,json.optJSONArray("Imports"));
         addStageIn(staging,json.optJSONArray("Stage in"));
		
         addStageOut(staging,json.optJSONArray("Exports"));
         addStageOut(staging,json.optJSONArray("Stage out"));
		
         jd.setDataStagingArray(staging.toArray(new DataStagingType[staging.size()]));
		
         try{
         jd=insertExecutionEnvironment(jd,json.optJSONObject("Execution environment"));
         }catch(Exception e){
         throw new IllegalArgumentException("Error adding execution environment",e);
         }

         */
        //build the final JSDL document
        JobDefinitionDocument job = JobDefinitionDocument.Factory.newInstance();
        job.addNewJobDefinition().setJobDescription(jd);
    }

}
