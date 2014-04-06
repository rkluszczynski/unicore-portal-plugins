package pl.plgrid.unicore.vasp.input;

import com.vaadin.ui.Button;
import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import eu.unicore.portal.core.Session;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.common.model.BrokerJobModel;
import pl.plgrid.unicore.common.ui.files.GenericInputFilePanel;
import pl.plgrid.unicore.common.utils.FileDataHelper;

import java.util.Set;

/**
 * Created by Rafal on 2014-04-05.
 */
class SubmitWorkAssignmentListener implements Button.ClickListener {
    private static final Logger logger = Logger.getLogger(SubmitWorkAssignmentListener.class);

    private final GenericInputFilePanel[] gifPanels;
    private final String[] tabSheetTitles;
    private final BrokerJobModel brokerJobModel;


    public SubmitWorkAssignmentListener(BrokerJobModel brokerJobModel, GenericInputFilePanel[] gifPanels, String[] tabSheetTitles) {
        this.brokerJobModel = brokerJobModel;
        this.gifPanels = gifPanels;
        this.tabSheetTitles = tabSheetTitles;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
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
        Set<String> inputFileSet = brokerJobModel.getInputFileSet();
        inputFileSet.clear();
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
                inputFileSet.add(fileUri);
            }
        } catch (Exception e) {
            logger.error("Problem during upload of file <" + filename
                    + "> to SMS!", e);
        }

        brokerJobModel.setStorageClient(storageClient);
        brokerJobModel.setWorkAssignmentID(workAssignmentID);

        brokerJobModel.submit();
    }
}
