package pl.plgrid.unicore.common.ui.model;

import org.w3.x2005.x08.addressing.EndpointReferenceType;

/**
 * @author Rafal
 */
public interface SimulationViewerData {

    public EndpointReferenceType getSimulationEpr();

    public EndpointReferenceType getDirectoryEpr();


    public String getStatus();

    public String getSimulationName();

    public String getSubmissionDate();

}
