package pl.plgrid.unicore.vasp.input;

import com.vaadin.ui.*;
import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.ui.Styles;
import org.apache.log4j.Logger;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.JobDefinitionDocument;
import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.common.tmp.JobDefinitionUtil;
import pl.plgrid.unicore.common.tmp.ServiceOrchestratorPortalClient;
import pl.plgrid.unicore.common.ui.ResourcesChooserPanel;
import pl.plgrid.unicore.common.ui.files.GenericInputFilePanel;
import pl.plgrid.unicore.common.utils.FileDataHelper;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Rafal on 2014-04-04.
 */
public class SubmissionPanel extends CustomComponent {
    private static final Logger logger = Logger.getLogger(SubmissionPanel.class);

    private GenericInputFilePanel[] gifPanels;
    private final String tabSheetTitles[] = {"INCAR", "KPOINTS", "POSCAR", "POTCAR"};


    public SubmissionPanel() {
        TabSheet tabSheet = createVASPFilesTabPanel(tabSheetTitles);
        tabSheet.setSizeFull();

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(60, Unit.PERCENTAGE);
        splitPanel.setFirstComponent(tabSheet);

        VerticalLayout verticalLayout = createBelowComponents();
        splitPanel.setSecondComponent(verticalLayout);

        setCompositionRoot(splitPanel);
        setSizeFull();
    }


    private VerticalLayout createBelowComponents() {
        Button submitWA = new Button("Submit VASP Job");
        submitWA.setStyleName(Styles.MARGIN_TOP_BOTTOM_15);
        submitWA.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

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
        });


        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(submitWA);
        vl.addComponent(new ResourcesChooserPanel());
        vl.setSizeFull();
        return vl;
    }


    private TabSheet createVASPFilesTabPanel(String[] tabSheetTitles) {
        gifPanels = new GenericInputFilePanel[tabSheetTitles.length];
        for (int i = 0; i < tabSheetTitles.length; ++i) {
            String txt = "<init>";
            txt = (i == 0) ? ExampleInputData.getINCAR()
                    : (i == 1) ? ExampleInputData.getKPOINTS()
                    : (i == 2) ? ExampleInputData.getPOSCAR()
                    : new Date().toString(); // ExampleInputData.getPOTCAR();
            gifPanels[i] = new GenericInputFilePanel(txt);
        }

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        for (int i = 0; i < tabSheetTitles.length; ++i) {
            tabSheet.addTab(gifPanels[i], tabSheetTitles[i]);
        }
        return tabSheet;
    }
}
