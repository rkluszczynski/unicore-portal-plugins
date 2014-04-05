package pl.plgrid.unicore.vasp.input;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import eu.unicore.portal.core.Session;
import org.apache.log4j.Logger;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.JobDefinitionDocument;
import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.common.tmp.JobDefinitionUtil;
import pl.plgrid.unicore.common.tmp.ServiceOrchestratorPortalClient;
import pl.plgrid.unicore.common.ui.files.GenericInputFilePanel;
import pl.plgrid.unicore.common.utils.FileDataHelper;

import java.util.ArrayList;

/**
 * Created by Rafal on 2014-04-05.
 */
class SubmitWorkAssignmentListener implements Button.ClickListener {
    private static final Logger logger = Logger.getLogger(SubmitWorkAssignmentListener.class);

    private final GenericInputFilePanel[] gifPanels;
    private final String[] tabSheetTitles;


    public SubmitWorkAssignmentListener(GenericInputFilePanel[] gifPanels, String[] tabSheetTitles) {
        this.gifPanels = gifPanels;
        this.tabSheetTitles = tabSheetTitles;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        String msg = "(empty)";
        String workAssignmentID = WSUtilities.newUniqueID();

        GridServicesExplorer gridServicesExplorer = Session.getCurrent().getServiceRegistry().getService(GridServicesExplorer.class);
        StorageClient storageClient = null;
        try {
            storageClient = gridServicesExplorer
                    .getStorageFactoryService()
                    .createClient();
        } catch (UnavailableGridServiceException e) {
            logger.error("ERROR", e);
            e.printStackTrace();
        }

        String filename = "";
        ArrayList<String> importLocations = new ArrayList<String>();
        try {
            for (int i = 0; i < tabSheetTitles.length; ++i) {
                filename = tabSheetTitles[i];
                String fileUri;
                if (!gifPanels[i].isGridLocation()) {
                    fileUri = FileDataHelper.importFileToGrid(
                            storageClient,
                            filename,
                            gifPanels[i].getFileContent()
                    );
                    logger.info("File from tab <" + tabSheetTitles[i]
                            + "> saved at location: <" + fileUri + ">");
                } else {
                    fileUri = gifPanels[i].getFileLocation();
                    logger.info("File from tab <" + tabSheetTitles[i]
                            + "> used from location: <" + fileUri + ">");
                }
                importLocations.add(fileUri);
            }
        } catch (Exception e) {
            logger.error("Problem during upload of file <" + filename
                    + "> to SMS!", e);
        }
        JobDefinitionDocument job = JobDefinitionUtil
                .createVASPJobDocument(importLocations);
        logger.info(job.toString());

        msg = new ServiceOrchestratorPortalClient().submitWorkAssignment(
                job, workAssignmentID, storageClient.getEPR());

        // TODO: handle missing application or version in SO

        Notification.show("Submitting VASP job...", msg,
                Notification.Type.TRAY_NOTIFICATION);
    }
}
