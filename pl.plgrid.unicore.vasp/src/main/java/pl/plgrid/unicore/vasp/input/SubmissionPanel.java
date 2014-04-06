package pl.plgrid.unicore.vasp.input;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.ui.Styles;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.model.BrokerJobModel;
import pl.plgrid.unicore.common.ui.ResourcesChooserPanel;
import pl.plgrid.unicore.common.ui.files.GenericInputFilePanel;
import pl.plgrid.unicore.common.utils.ClassPathResource;
import pl.plgrid.unicore.vasp.i18n.VASPViewI18N;

import java.util.Date;

/**
 * Created by Rafal on 2014-04-04.
 */
public class SubmissionPanel extends CustomComponent {
    private static final Logger logger = Logger.getLogger(SubmissionPanel.class);


    // TODO: maybe also without brokerJobModel
    public SubmissionPanel(BrokerJobModel brokerJobModel) {
        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(60, Unit.PERCENTAGE);

        TabSheet tabSheet = createVASPFilesTabPanel(brokerJobModel);
        splitPanel.setFirstComponent(tabSheet);

        AbstractLayout rightPanel = createUserManagementPanel(brokerJobModel);
        splitPanel.setSecondComponent(rightPanel);

        setCompositionRoot(splitPanel);
        setSizeFull();
    }


    private AbstractLayout createUserManagementPanel(final BrokerJobModel brokerJobModel) {
        GridLayout gridLayout = new GridLayout(1, 3);

        Button submitWorkAssignmentButton = new Button(getMessage("submitButton"));
        submitWorkAssignmentButton.setStyleName(Styles.MARGIN_TOP_BOTTOM_15);
        submitWorkAssignmentButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                brokerJobModel.submit();
            }
        });
        gridLayout.addComponent(submitWorkAssignmentButton, 0, 0);
        gridLayout.setComponentAlignment(submitWorkAssignmentButton, Alignment.MIDDLE_CENTER);

        ResourcesChooserPanel resourcesChooserPanel = new ResourcesChooserPanel(brokerJobModel);
        gridLayout.addComponent(resourcesChooserPanel, 0, 1);
        gridLayout.setComponentAlignment(resourcesChooserPanel, Alignment.TOP_CENTER);
        gridLayout.setRowExpandRatio(1, 1.f);

        ClassPathResource pathResource = new ClassPathResource("vasp-logo-full.png");
//        ClassResource pathResource = new ClassResource("vasp-logo-alpha.png");
        Image logoImage = new Image("", pathResource);
        gridLayout.addComponent(logoImage, 0, 2);
        gridLayout.setComponentAlignment(logoImage, Alignment.MIDDLE_CENTER);
        gridLayout.setRowExpandRatio(2, 1.f);

        gridLayout.setSizeFull();
        return gridLayout;
    }


    private TabSheet createVASPFilesTabPanel(BrokerJobModel brokerJobModel) {
        String tabSheetTitles[] = {"INCAR", "KPOINTS", "POSCAR", "POTCAR"};
        GenericInputFilePanel[] gifPanels;

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
        tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);
        for (int i = 0; i < tabSheetTitles.length; ++i) {
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
