package pl.plgrid.unicore.vasp.input;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.ui.Styles;
import pl.plgrid.unicore.common.model.BrokerJobModel;
import pl.plgrid.unicore.common.ui.ResourcesManagementPanel;
import pl.plgrid.unicore.common.ui.files.GenericInputFilePanel;
import pl.plgrid.unicore.portal.core.utils.ClassPathResource;
import pl.plgrid.unicore.vasp.i18n.VASPViewI18N;

import java.util.Date;
import java.util.Set;

/**
 * @author Rafal
 */
public class SubmissionPanel extends CustomComponent {

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
//        final ResourcesOnTopPanel resourcesOnTopPanel = null;

        int gridLayoutRowNumber = 0;
        Button submitWorkAssignmentButton = new Button(getMessage("submitButton"));
        submitWorkAssignmentButton.setStyleName(Styles.MARGIN_TOP_BOTTOM_15);
        submitWorkAssignmentButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String simulationName = simulationNameTextField.getValue();
                if (simulationName == null || simulationName.compareTo("") == 0) {
                    simulationName = "VASP Simulation submitted by U7Portal";
                }
                if (resourcesOnTopPanel != null) {
                    brokerJobModel.getResourceSet().putAll(
                            resourcesOnTopPanel.getResources()
                    );
                }
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
        ResourcesManagementPanel resourcesManagementPanel = new ResourcesManagementPanel(
                brokerJobModel,
//                resourcesOnTopPanel,
                null,
                excludeResourceNames
        );
        gridLayout.addComponent(resourcesManagementPanel, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(resourcesManagementPanel, Alignment.TOP_CENTER);
        gridLayout.setRowExpandRatio(gridLayoutRowNumber, 1.f);

        ++gridLayoutRowNumber;
        ClassPathResource pathResource = new ClassPathResource("vasp-logo-full.png");
//        ClassResource pathResource = new ClassResource("vasp-logo-alpha.png");
        Image logoImage = new Image("", pathResource);
        gridLayout.addComponent(logoImage, 0, gridLayoutRowNumber);
        gridLayout.setComponentAlignment(logoImage, Alignment.MIDDLE_CENTER);
        gridLayout.setRowExpandRatio(gridLayoutRowNumber, 1.f);

        gridLayout.setSizeFull();
        return gridLayout;
    }


    private TabSheet createVASPFilesTabPanel(final BrokerJobModel brokerJobModel) {
        String tabSheetTitles[] = {"INCAR", "KPOINTS", "POSCAR", "POTCAR"};
        GenericInputFilePanel[] gifPanels =
                new GenericInputFilePanel[tabSheetTitles.length];

        final ObjectProperty<String> uploadFolderTextFieldProperty = new ObjectProperty<String>("");
        for (int i = 0; i < tabSheetTitles.length; ++i) {
            String textContent = (i == 0) ? ExampleInputData.getINCAR()
                    : (i == 1) ? ExampleInputData.getKPOINTS()
                    : (i == 2) ? ExampleInputData.getPOSCAR()
                    : new Date().toString(); // ExampleInputData.getPOTCAR();
            gifPanels[i] = new GenericInputFilePanel(textContent, uploadFolderTextFieldProperty);
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

    private String getMessage(String messageKey) {
        return GlobalState.getMessage(VASPViewI18N.ID, "vasp.caption." + messageKey);
    }
}
