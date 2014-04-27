package pl.plgrid.unicore.common.model;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import eu.unicore.jsdl.extensions.IgnoreFailureDocument;
import eu.unicore.jsdl.extensions.ResourceRequestDocument;
import eu.unicore.portal.core.Session;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.*;
import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.common.resources.StandardResources;
import pl.plgrid.unicore.common.ui.model.GridInputFileComponent;
import pl.plgrid.unicore.common.utils.FileDataHelper;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;

import java.util.List;
import java.util.Map;

abstract class AbstractJobModel {
    private static final Logger logger = Logger.getLogger(AbstractJobModel.class);

    private final String applicationName;
    private final String applicationVersion;

    protected final String workAssignmentID;
    protected StorageClient storageClient;

    protected final Map<String, String> parameterSet = Maps.newConcurrentMap();
    protected final Map<String, String> resourceSet = Maps.newConcurrentMap();
    //    protected final Map<String, ResourceSetComponent> resourceComponentMap = Maps.newConcurrentMap();
    protected final Map<String, GridInputFileComponent> inputFileSet = Maps.newConcurrentMap();
    protected final Map<String, String> outputFileSet = Maps.newConcurrentMap();

    protected AbstractJobModel(String applicationName, String applicationVersion) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        workAssignmentID = WSUtilities.newUniqueID();
    }


    public abstract void submit(String jobName);


    public void registerGridInputFileComponent(String filename, GridInputFileComponent component) {
        inputFileSet.put(filename, component);
    }

//    public void registerResourceComponent(String resourceName, ResourceSetComponent component) {
//        resourceComponentMap.put(resourceName, component);
//    }

    public Map<String, String> getResourceSet() {
        return resourceSet;
    }


    private List<DataStagingType> createDataStagingFragment() {
        List<DataStagingType> dataStagingList = Lists.newArrayList();
        for (Map.Entry<String, GridInputFileComponent> entry : inputFileSet.entrySet()) {
            String filename = entry.getKey();
            GridInputFileComponent.GridInputFileData inputFileData = entry
                    .getValue()
                    .getInputFileData();

            DataStagingDocument dsd = DataStagingDocument.Factory.newInstance();
            DataStagingType d = dsd.addNewDataStaging();
            d.setFileName(filename);
            d.setCreationFlag(CreationFlagEnumeration.OVERWRITE);

            if (inputFileData.getType() == GridInputFileComponent.GridInputFileValueType.VALUE_CONTENT) {
                try {
                    String fileUri = FileDataHelper.importFileToGrid(
                            getStorageClient(),
                            filename,
                            inputFileData.getData()
                    );
                    d.addNewSource().setURI(fileUri);
                    logger.info("File from tab <" + filename
                            + "> saved at location: <" + fileUri + ">");
                } catch (Exception e) {
                    logger.error("Problem during upload of file <" + filename + "> to SMS!", e);
                }
            } else if (inputFileData.getType() == GridInputFileComponent.GridInputFileValueType.VALUE_GRID_PATH) {
                d.addNewSource().setURI(inputFileData.getData());
                logger.info("File from tab <" + filename
                        + "> used from location: <" + inputFileData.getData() + ">");
            } else {
                throw new IllegalArgumentException("Unknown GridInputFileData type!");
            }

            IgnoreFailureDocument ifd = IgnoreFailureDocument.Factory.newInstance();
            ifd.setIgnoreFailure(false);

            WSUtilities.append(ifd, dsd);
            dataStagingList.add(d);
        }
        return dataStagingList;
    }


    public JobDefinitionDocument prepareJobDefinitionDocument(
            String jobName
    ) {
        JobDescriptionType jobDesc = JobDescriptionType.Factory.newInstance();
        ApplicationDocument appDoc = ApplicationDocument.Factory.newInstance();
        ApplicationType app = appDoc.addNewApplication();
        app.setApplicationName(applicationName);
        app.setApplicationVersion(applicationVersion);
        jobDesc.setApplication(app);

        jobDesc.addNewJobIdentification().setJobName(jobName);

        List<DataStagingType> dataStaging = createDataStagingFragment();
        jobDesc.setDataStagingArray(dataStaging.toArray(new DataStagingType[dataStaging.size()]));

        try {
            jobDesc.setResources(makeResources());
        } catch (Exception e) {
            logger.error("Error setting job resources!", e);
        }

        JobDefinitionDocument jobDefinitionDocument = JobDefinitionDocument.Factory.newInstance();
        jobDefinitionDocument.addNewJobDefinition().setJobDescription(jobDesc);

        return jobDefinitionDocument;

    }

    private StorageClient getStorageClient() {
        if (storageClient == null) {
            synchronized (workAssignmentID) {
                if (storageClient == null) {
                    try {
                        GridServicesExplorer gridServicesExplorer = Session
                                .getCurrent()
                                .getServiceRegistry()
                                .getService(GridServicesExplorer.class);
                        storageClient = gridServicesExplorer
                                .getStorageFactoryService()
                                .createClient();
                    } catch (UnavailableGridServiceException e) {
                        logger.error("ERROR", e);
                        e.printStackTrace();
                    }
                }
            }
        }
        return storageClient;
    }

    private ResourcesType makeResources() throws Exception {
        ResourcesDocument rd = ResourcesDocument.Factory.newInstance();
        ResourcesType rt = rd.addNewResources();

        for (Map.Entry<String, String> entry : resourceSet.entrySet()) {
            String resourceName = entry.getKey();
            String resourceValue = entry.getValue();

            logger.info("[[makeResources]] =>  " + resourceName + " = " + resourceValue);
//
//            ResourceSetComponent resourceSetComponent = resourceComponentMap.get(resourceName);
//            if (resourceSetComponent != null) {
//                resourceValue = resourceSetComponent.getResourceValue();
//                logger.info("[[makeResources]] ..  " + resourceName + " = " + resourceValue);
//            }

            if (resourceName.equals("Reservation")) {
                try {
                    String reservationID = resourceValue;
                    insertReservationID(reservationID, rd);
                } catch (Exception e) {
                    logger.error("Error processing reservation id", e);
                }
            } else if (resourceName.equals("Operating system")) {
                String os = resourceValue;//JSONUtil.getString(j,"Operating system");

                if (os != null) {
                    OperatingSystemTypeEnumeration.Enum osType = getOSType(os);
                    if (osType != null) {
                        rt.addNewOperatingSystem().addNewOperatingSystemType().setOperatingSystemName(osType);
                    } else {
                        logger.error("Operating system " + os + " not recognized (not defined in JSDL).", null);
                    }
                }
            } else if (resourceName.equals("Runtime") || resourceName.equals(StandardResources.individualCPUTime.name())) {
                String runtime = resourceValue;//j.getString("Runtime");

                if (runtime != null) {
//                    TODO:
//                    runtime=String.valueOf(UnitParser.getTimeParser(1).getDoubleValue(runtime));
                    rt.addNewIndividualCPUTime().addNewExact().setStringValue(runtime);
                }

            } else if (resourceName.equals("Memory") || resourceName.equals(StandardResources.individualPhysicalMemory.name())) {
                String memory = resourceValue;//j.getString("Memory");

                if (memory != null) {
//                    TODO:
//                    memory=String.valueOf(UnitParser.getCapacitiesParser(1).getDoubleValue(memory));
                    rt.addNewIndividualPhysicalMemory().addNewExact().setStringValue(memory);
                }

            } else if (resourceName.equals("CPUs") || resourceName.equals(StandardResources.totalNumberOfCPUs.name())) {
                String totalCPUs = resourceValue;//j.getString("CPUs");

                if (totalCPUs != null) {
                    rt.addNewTotalCPUCount().addNewExact().setStringValue(totalCPUs);
                }

            } else if (resourceName.equals("Nodes") || resourceName.equals(StandardResources.totalNumberOfNodes.name())) {
                String nodes = resourceValue;//j.getString("Nodes");

                if (nodes != null) {
                    rt.addNewTotalResourceCount().addNewExact().setStringValue(nodes);
                }

            } else if (resourceName.equals("CPUsPerNode") || resourceName.equals(StandardResources.individualNumberOfCPUs.name())) {
                String cpus = resourceValue;//j.getString("CPUsPerNode");

                if (cpus != null) {
                    rt.addNewIndividualCPUCount().addNewExact().setStringValue(cpus);
                }

            } else {//generic resource
                try {
                    String req = resourceValue;//j.getString(resource);
                    insertResourceRequest(resourceName, req, rd);
                } catch (Exception e) {
                    logger.error("Error processing resource request for <" + resourceName + ">", e);
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
