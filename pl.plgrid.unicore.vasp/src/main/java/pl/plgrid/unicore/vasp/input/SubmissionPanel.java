package pl.plgrid.unicore.vasp.input;

import com.google.common.base.Strings;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.ui.Styles;
import pl.plgrid.unicore.common.model.BrokerJobModel;
import pl.plgrid.unicore.common.ui.files.GenericInputFilePanel;
import pl.plgrid.unicore.portal.core.utils.ClassPathResource;
import pl.plgrid.unicore.vasp.i18n.VASPViewI18N;

import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * @author Rafal
 */
public class SubmissionPanel extends CustomComponent {
    public static final String VASP_SIMULATION_DEFAULT_PREFIX = "vasp-simulation__";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm");
    private TextField simulationNameTextField;

    // TODO: try to design api to do it without passing brokerJobModel (?)

    public SubmissionPanel(BrokerJobModel brokerJobModel, Set<String> excludeResourceNames) {
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.addStyleName(Reindeer.SPLITPANEL_SMALL);
        splitPanel.setSplitPosition(60, Unit.PERCENTAGE);
        splitPanel.setMinSplitPosition(60, Unit.PERCENTAGE);
        splitPanel.setMaxSplitPosition(80, Unit.PERCENTAGE);

        VerticalLayout tabSheetPanel = new VerticalLayout(
                createVASPFilesTabPanel(brokerJobModel)
        );
        tabSheetPanel.setMargin(new MarginInfo(false, true, false, false));
        tabSheetPanel.setSizeFull();
        splitPanel.setFirstComponent(tabSheetPanel);

        AbstractLayout rightPanel = createUserManagementPanel(brokerJobModel, excludeResourceNames);
        splitPanel.setSecondComponent(rightPanel);

        setCompositionRoot(splitPanel);
        setSizeFull();
    }

    private AbstractLayout createUserManagementPanel(final BrokerJobModel brokerJobModel, Set<String> excludeResourceNames) {
        GridLayout gridLayout = new GridLayout(1, 4);
        final ResourcesOnTopPanel resourcesOnTopPanel = new ResourcesOnTopPanel();
        final FixedResourcesPanel fixedResourcesPanel = new FixedResourcesPanel();
//        final ResourcesOnTopPanel resourcesOnTopPanel = null;

        int gridLayoutRowNumber = 0;
        Button submitWorkAssignmentButton = new Button(getMessage("submitButton"));
        submitWorkAssignmentButton.setStyleName(Styles.MARGIN_TOP_BOTTOM_15);
        submitWorkAssignmentButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String simulationName = VASP_SIMULATION_DEFAULT_PREFIX + simulationNameTextField.getValue();
                if (Strings.isNullOrEmpty(simulationName)) {
                    simulationName += "Simulation submitted by UNICORE Portal";
                }
                if (resourcesOnTopPanel != null) {
                    brokerJobModel.getResourceSet().putAll(
                            resourcesOnTopPanel.getResources()
                    );
                }
                brokerJobModel.getResourceSet().putAll(
                        fixedResourcesPanel.getResources()
                );

                brokerJobModel.submit(simulationName);
            }
        });
        gridLayout.addComponent(submitWorkAssignmentButton, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(submitWorkAssignmentButton, Alignment.MIDDLE_CENTER);

        ++gridLayoutRowNumber;
        simulationNameTextField = new TextField(getMessage("simulationNameCaption"));
        simulationNameTextField.addStyleName(Styles.MARGIN_TOP_BOTTOM_15);
        FormLayout simulationNameFormLayout = new FormLayout();
        simulationNameFormLayout.setSpacing(true);
        simulationNameFormLayout.setMargin(true);
        simulationNameFormLayout.addComponent(simulationNameTextField);
        gridLayout.addComponent(simulationNameFormLayout, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(simulationNameFormLayout, Alignment.MIDDLE_LEFT);

        ++gridLayoutRowNumber;
        gridLayout.addComponent(fixedResourcesPanel, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(fixedResourcesPanel, Alignment.TOP_CENTER);
        gridLayout.setRowExpandRatio(gridLayoutRowNumber, 0.6f);

//        ++gridLayoutRowNumber;
//        ResourcesManagementPanel resourcesManagementPanel = new ResourcesManagementPanel(
//                brokerJobModel,
////                resourcesOnTopPanel,
//                null,
//                excludeResourceNames
//        );
//        gridLayout.addComponent(resourcesManagementPanel, 0, gridLayoutRowNumber);
//        gridLayout.setComponentAlignment(resourcesManagementPanel, Alignment.TOP_CENTER);
//        gridLayout.setRowExpandRatio(gridLayoutRowNumber, 1.f);

        ++gridLayoutRowNumber;
        ClassPathResource pathResource = new ClassPathResource("vasp-logo-midi.png");
//        ClassPathResource pathResource = new ClassPathResource("vasp-logo-full.png");
//        ClassResource pathResource = new ClassResource("vasp-logo-alpha.png");
        Image logoImage = new Image("", pathResource);
        gridLayout.addComponent(logoImage, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(logoImage, Alignment.MIDDLE_CENTER);
        gridLayout.setRowExpandRatio(gridLayoutRowNumber, 0.2f);

        gridLayout.setSizeFull();
        return gridLayout;
    }


    private TabSheet createVASPFilesTabPanel(final BrokerJobModel brokerJobModel) {
        String tabSheetTitles[] = {"INCAR", "KPOINTS", "POSCAR", "POTCAR"};
        GenericInputFilePanel[] gifPanels = new GenericInputFilePanel[tabSheetTitles.length];

        final ObjectProperty<String> uploadFolderTextFieldProperty = new ObjectProperty<String>("");
        for (int i = 0; i < tabSheetTitles.length; ++i) {
            gifPanels[i] = new GenericInputFilePanel(getInputFileContent(i), uploadFolderTextFieldProperty);
        }

        TabSheet tabSheet = new TabSheet();
        tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);
        for (int i = 0; i < tabSheetTitles.length; ++i) {
            gifPanels[i].setSpacing(true);
            tabSheet.addTab(gifPanels[i], tabSheetTitles[i]);

            brokerJobModel.registerGridInputFileComponent(tabSheetTitles[i], gifPanels[i]);
        }
        tabSheet.setSizeFull();
        return tabSheet;
    }

    private String getInputFileContent(int i) {
        String prefix = "vasp.input";
        String content = "";
        if (i == 0) {
            content = null; //GlobalState.getMessage(VASPViewI18N.ID, String.format("%s.incar", prefix));
            return content == null ? ExampleInputData.getINCAR() : content;
        } else if (i == 1) {
            content = null; //GlobalState.getMessage(VASPViewI18N.ID, String.format("%s.kpoints", prefix));
            return content == null ? ExampleInputData.getKPOINTS() : content;
        } else if (i == 2) {
            content = null; //GlobalState.getMessage(VASPViewI18N.ID, String.format("%s.poscar", prefix));
            return content == null ? ExampleInputData.getPOSCAR() : content;
        } else if (i == 3) {
            content = null; //GlobalState.getMessage(VASPViewI18N.ID, String.format("%s.potcar", prefix));
            return content == null ?
                    "https://unicore.studmat.umk.pl:8080/PLG-NCU-TEST/services/StorageManagement?res=default_storage#.vasp-default-input/POTCAR"
                    : content;
        }
        return content;
    }

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(VASPViewI18N.ID, "vasp.caption." + messageKey);
    }
}
