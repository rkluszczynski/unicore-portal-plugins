package pl.plgrid.unicore.vasp;

import com.vaadin.ui.*;
import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.ui.PortalApplication;
import eu.unicore.portal.ui.Styles;
import org.apache.log4j.Logger;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.JobDefinitionDocument;
import pl.plgrid.unicore.common.ui.AvailableResourcesPanel;
import pl.plgrid.unicore.common.ui.JobsTableViewer;
import pl.plgrid.unicore.vasp.input.ExampleInputData;
import pl.plgrid.unicore.vasp.session.UserSessionGridState;
import pl.plgrid.unicore.vasp.ui.GenericInputFilePanel;
import pl.plgrid.unicore.vasp.utils.JobDefinitionUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author rkluszczynski
 */
public class VASPMainPanel extends VerticalLayout {

    private static final Logger logger = Logger.getLogger(VASPMainPanel.class);

    private final UserSessionGridState userGridState;
    private final String tabSheetTitles[] = {"INCAR", "KPOINTS", "POSCAR", "POTCAR"};
    
    private GenericInputFilePanel[] gifPanels;
        
    public VASPMainPanel() {
        super();

        logger.info("Creating VASP view for user: " + Session.getCurrent().getUser().getUsername());
        userGridState = new UserSessionGridState();
        createMainViewComponents();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible == true) {
            super.setVisible(true);
        } else {
            super.setVisible(false);
        }
    }

    private void createMainViewComponents() {
        setSizeFull();
        addStyleName(Styles.PADDING_All_10);

        HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        hsplit.setSplitPosition(50, Unit.PERCENTAGE);
        // hsplit.setLocked(true);
        addComponent(hsplit);

        TabSheet tabSheet = createVASPFilesTabPanel(tabSheetTitles);
        tabSheet.setSizeFull();
        hsplit.setFirstComponent(tabSheet);

        final JobsTableViewer jobsTableViewer = new JobsTableViewer();

        Button submitWA = new Button("Submit VASP Job");
        submitWA.setStyleName(Styles.MARGIN_TOP_BOTTOM_15);
        submitWA.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                String msg = "(empty)";
                String workAssignmentID = WSUtilities.newUniqueID();
                StorageClient storageClient = userGridState
                        .getServiceClientsFactory()
                        .getStorageFactoryClientSMS();

                String filename = "";
                ArrayList<String> importLocations = new ArrayList<String>();
                try {
                    for (int i = 0; i < tabSheetTitles.length; ++i) {
                        filename = tabSheetTitles[i];
                        String fileUri;
                        if (! gifPanels[i].isGridLocation()) {
                            fileUri = userGridState.importFileToGrid(
                                    storageClient, 
                                    filename, 
                                    gifPanels[i].getFileContent(),
                                    workAssignmentID);                
                            logger.info("File from tab <" + tabSheetTitles[i]
                                    + "> saved at location: <" + fileUri + ">");
                        }
                        else {
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
                msg = userGridState.getSoPortalClient().submitWorkAssignment(
                        job, workAssignmentID, storageClient.getEPR());
                
                // TODO: handle missing application or version in SO
                
                Notification.show("Submitting VASP job...", msg,
                        Notification.Type.TRAY_NOTIFICATION);
            }
        });

        Button showResourcesButton = new Button("Show Resources");
        showResourcesButton.setStyleName(Styles.MARGIN_TOP_BOTTOM_15);
        showResourcesButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Window w = new Window("Available Resources");
                PortalApplication.getCurrent().addWindow(w);
                w.setContent(new AvailableResourcesPanel());
                w.center();

//                userGridState
//                        .gatherAllAvailableResources(new UserSessionGridState.UIResourcesUpdater() {
//
//                            @Override
//                            public void updateUI(
//                                    ConcurrentMap<String, ConcurrentHashMap<AvailableResourceTypeType.Enum, ArrayList<AvailableResourceType>>> m,
//                                    boolean isFinished) {
//                                        if (!isFinished) {
//                                            return;
//                                        }
//                                        AvailableResourcePanel aResourcePanel = new AvailableResourcePanel();
//                                        aResourcePanel.setResources(m);
//
//                                        Window w = new Window("Available Resources");
//                                        PortalApplication.getCurrent().addWindow(w);
//                                        w.setContent(aResourcePanel);
//                                        w.center();
//                                    }
//                        });

            }
        });

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(submitWA);
        hl.addComponent(showResourcesButton);

        VerticalLayout vl = new VerticalLayout();        
        vl.setSizeFull();
        vl.addComponent(hl);
        vl.setExpandRatio(hl, 1f);
        vl.addComponent(jobsTableViewer);
        vl.setExpandRatio(jobsTableViewer, 10f);
        
        hsplit.setSecondComponent(vl);
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
