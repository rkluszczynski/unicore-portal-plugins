package pl.plgrid.unicore.vasp.client;

import java.io.FilenameFilter;
import java.util.ArrayList;

import de.fzj.unicore.uas.JobManagement;

public interface JobManagementPortalClient {
	
	public ArrayList<JobManagement> getGridJobs();

	public ArrayList<JobManagement> getGridJobs(int start, int end);

	public ArrayList<JobManagement> getGridJobs(FilenameFilter filter);

	public ArrayList<JobManagement> getTargetSystemJobs();

	public ArrayList<JobManagement> getTargetSystemJobs(int start, int end);

	public ArrayList<JobManagement> getTargetSystemJobs(FilenameFilter filter);	
	
}
