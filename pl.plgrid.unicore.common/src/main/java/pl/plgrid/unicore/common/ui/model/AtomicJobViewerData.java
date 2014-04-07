package pl.plgrid.unicore.common.ui.model;

import org.unigrids.services.atomic.types.StatusType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Rafal
 */
public class AtomicJobViewerData implements SimulationViewerData {
    private EndpointReferenceType simulationEpr;
    private EndpointReferenceType directoryEpr;

    private String status;
    private String simulationName;
    private String submissionDate;

    public AtomicJobViewerData(EndpointReferenceType simulationEpr) {
        this.simulationEpr = simulationEpr;

        simulationName = simulationEpr.getAddress().getStringValue().split("=")[1];
    }


    public void setDirectoryEpr(EndpointReferenceType directoryEpr) {
        this.directoryEpr = directoryEpr;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    public void setSubmissionDate(Date submissionDate) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        this.submissionDate = sdf.format(submissionDate);
    }


    @Override
    public EndpointReferenceType getSimulationEpr() {
        return simulationEpr;
    }

    @Override
    public EndpointReferenceType getDirectoryEpr() {
        return directoryEpr;
    }

    @Override
    public String getStatus() {
        return (status == null) ? StatusType.UNDEFINED.toString() : status;
    }

    @Override
    public String getSimulationName() {
        return simulationName;
    }

    @Override
    public String getSubmissionDate() {
        return (submissionDate == null) ? "< UNKNOWN >" : submissionDate;
    }
}
