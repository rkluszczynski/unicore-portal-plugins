package pl.plgrid.unicore.vasp.tmp;

import de.fzj.unicore.uas.JobManagement;

import java.io.FilenameFilter;
import java.util.ArrayList;

public interface JobManagementPortalClient {

    public ArrayList<JobManagement> getGridJobs();

    public ArrayList<JobManagement> getGridJobs(int start, int end);

    public ArrayList<JobManagement> getGridJobs(FilenameFilter filter);

    public ArrayList<JobManagement> getTargetSystemJobs();

    public ArrayList<JobManagement> getTargetSystemJobs(int start, int end);

    public ArrayList<JobManagement> getTargetSystemJobs(FilenameFilter filter);

}
