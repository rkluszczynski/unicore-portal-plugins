package pl.plgrid.unicore.common.model;

import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;
import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import eu.unicore.jsdl.extensions.IgnoreFailureDocument;
import eu.unicore.jsdl.extensions.ResourceRequestDocument;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class AbstractJobModel {
    private static final Logger logger = Logger.getLogger(AbstractJobModel.class);


    protected String applicationName = "";
    protected String applicationVersion = "";

    protected final Map<String, String> parametersMap = Maps.newConcurrentMap();

    public Set<String> getInputFileSet() {
        return inputFileSet;
    }

    protected final Set<String> inputFileSet = Sets.newHashSet();
    protected final Set<String> outputFileSet = Sets.newHashSet();

    protected final Map<String, String> resourceSet = Maps.newConcurrentMap();


    public abstract void submit();


    public Map<String, String> getResourceSet() {
        return resourceSet;
    }


    public JobDefinitionDocument prepareJobDefinitionDocument(
            String jobName,
            Set<String> jobImports
    ) {
        JobDescriptionType jobDesc = JobDescriptionType.Factory.newInstance();
        ApplicationDocument appDoc = ApplicationDocument.Factory.newInstance();
        ApplicationType app = appDoc.addNewApplication();
        app.setApplicationName(applicationName);
        app.setApplicationVersion(applicationVersion);
        jobDesc.setApplication(app);

        jobDesc.addNewJobIdentification().setJobName(jobName);

        List<DataStagingType> staging = new ArrayList<DataStagingType>();
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

        try {
            jobDesc.setResources(makeResources());
        } catch (Exception e) {
            logger.error("Error setting job resources!", e);
        }

        JobDefinitionDocument jobDefinitionDocument = JobDefinitionDocument.Factory.newInstance();
        jobDefinitionDocument.addNewJobDefinition().setJobDescription(jobDesc);

        return jobDefinitionDocument;

    }


    private ResourcesType makeResources() throws Exception {
        ResourcesDocument rd = ResourcesDocument.Factory.newInstance();
        ResourcesType rt = rd.addNewResources();

        for (Map.Entry<String, String> entry : resourceSet.entrySet()) {
            logger.info("makeResources =>  " + entry.getKey() + " = " + entry.getValue());

            String resource = entry.getKey();
            if ("Reservation".equals(resource)) {
                try {
                    String reservationID = entry.getValue();
                    insertReservationID(reservationID, rd);
                } catch (Exception e) {
                    logger.error("Error processing reservation id", e);
                }
            } else if ("Operating system".equals(resource)) {
                String os = entry.getValue();//JSONUtil.getString(j,"Operating system");
                if (os != null) {
                    OperatingSystemTypeEnumeration.Enum osType = getOSType(os);
                    if (osType != null) {
                        rt.addNewOperatingSystem().addNewOperatingSystemType().setOperatingSystemName(osType);
                    } else {
                        logger.error("Operating system " + os + " not recognized (not defined in JSDL).", null);
                    }
                }
            } else if ("Runtime".equals(resource)) {
                String runtime = entry.getValue();//j.getString("Runtime");
                if (runtime != null) {
//                    TODO:
//                    runtime=String.valueOf(UnitParser.getTimeParser(1).getDoubleValue(runtime));
                    rt.addNewIndividualCPUTime().addNewExact().setStringValue(runtime);
                }
            } else if ("Memory".equals(resource)) {
                String memory = entry.getValue();//j.getString("Memory");
                if (memory != null) {
//                    TODO:
//                    memory=String.valueOf(UnitParser.getCapacitiesParser(1).getDoubleValue(memory));
                    rt.addNewIndividualPhysicalMemory().addNewExact().setStringValue(memory);
                }
            } else if ("CPUs".equals(resource)) {
                String totalCPUs = entry.getValue();//j.getString("CPUs");
                if (totalCPUs != null) {
                    rt.addNewTotalCPUCount().addNewExact().setStringValue(totalCPUs);
                }
            } else if ("Nodes".equals(resource)) {
                String nodes = entry.getValue();//j.getString("Nodes");
                if (nodes != null) {
                    rt.addNewTotalResourceCount().addNewExact().setStringValue(nodes);
                }
            } else if ("CPUsPerNode".equals(resource)) {
                String cpus = entry.getValue();//j.getString("CPUsPerNode");
                if (cpus != null) {
                    rt.addNewIndividualCPUCount().addNewExact().setStringValue(cpus);
                }
            } else {
//generic resource
                try {
                    String req = entry.getValue();//j.getString(resource);
                    insertResourceRequest(resource, req, rd);
                } catch (Exception e) {
                    logger.error("Error processing resource request for <" + resource + ">", e);
                }
            }
        }
        return rt;
    }

    private void insertReservationID(String id, ResourcesDocument target) throws Exception {
        String resID = "<u6rr:ReservationReference xmlns:u6rr=\"http://www.unicore.eu/unicore/xnjs\">" + id + "</u6rr:ReservationReference>";
        XmlObject o = XmlObject.Factory.parse(resID);
//and append to resources doc...
        WSUtilities.append(o, target);
    }

    private void insertResourceRequest(String name, String value, ResourcesDocument target) throws Exception {
        ResourceRequestDocument rrd = ResourceRequestDocument.Factory.newInstance();
        rrd.addNewResourceRequest().setName(name);
        rrd.getResourceRequest().setValue(value);
        WSUtilities.append(rrd, target);
    }

    //list of common OSs for which we want to ignore case
    static final String[] knownOSs = new String[]{"LINUX", "MACOS", "AIX",
            "FreeBSD", "NetBSD", "Solaris", "WINNT", "IRIX", "HPUX", "Unknown"};

    OperatingSystemTypeEnumeration.Enum getOSType(String os) {
        for (String o : knownOSs) {
            if (o.equalsIgnoreCase(os)) {
                return OperatingSystemTypeEnumeration.Enum.forString(o);
            }
        }
        return OperatingSystemTypeEnumeration.Enum.forString(os);
    }

}
