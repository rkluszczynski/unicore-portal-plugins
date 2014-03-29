package pl.plgrid.unicore.vasp.input;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import eu.unicore.portal.ui.PortalApplication;
import eu.unicore.portal.ui.Styles;
import org.unigrids.services.atomic.types.ProtocolType;
import pl.plgrid.unicore.vasp.ui.FileInStorageChooser;

/**
 *
 * @author rkluszczynski
 */
@SuppressWarnings("serial")
public class GenericInputFilePanel extends VerticalLayout implements
        Button.ClickListener, LayoutClickListener {

    private static final String LABEL_TOGGLE_TO_FILE_BROWSER = "<u><i>Toggle to File Browser</i></u>";
    private static final String LABEL_TOGGLE_TO_FILE_CONTENT = "<u><i>Toggle to File Content</i></u>";
    private static final String BUTTON_TEXT_BROWSE_GRID = "Browse Grid";
    private static final String CAPTION_CHOOSE_GRID_FILE = "Choose Grid File";
    private static final String CAPTION_GRID_FILE = " Grid File: ";

    private final GridLayout browseGridFileRowPanel;
    private final Label switchLabel;
    private final TextField gridFilePathTextField;
    private final TextArea fileContentTextArea;
    private final Button gridFileChooserButton;

    protected FileInStorageChooser fileChooser = null;

    public GenericInputFilePanel(String content) {
        HorizontalLayout switchRowLayout = new HorizontalLayout();
        switchRowLayout.setWidth(100f, Unit.PERCENTAGE);
        switchRowLayout.setHeight(SIZE_UNDEFINED, Unit.EM);
        switchRowLayout.setImmediate(true);
        switchLabel = new Label(LABEL_TOGGLE_TO_FILE_BROWSER, ContentMode.HTML);
        switchLabel.setSizeUndefined();
        switchRowLayout.addComponent(switchLabel);
        switchRowLayout.setComponentAlignment(switchLabel, Alignment.MIDDLE_RIGHT);
        switchRowLayout.addLayoutClickListener(this);

        browseGridFileRowPanel = new GridLayout(3, 1);
        browseGridFileRowPanel.setWidth(100f, Unit.PERCENTAGE);
        browseGridFileRowPanel.setHeight(SIZE_UNDEFINED, Unit.EM);
        browseGridFileRowPanel.setVisible(false);
        browseGridFileRowPanel.setSpacing(true);
        Label gridFileCaptionLabel = new Label(CAPTION_GRID_FILE);
        gridFileCaptionLabel.setSizeUndefined();
        browseGridFileRowPanel.addComponent(gridFileCaptionLabel, 0, 0);
        browseGridFileRowPanel.setColumnExpandRatio(0, 0.0f);
        gridFilePathTextField = new TextField();
        gridFilePathTextField.setSizeFull();
        gridFilePathTextField.setEnabled(false);
        browseGridFileRowPanel.addComponent(gridFilePathTextField, 1, 0);
        browseGridFileRowPanel.setColumnExpandRatio(1, 1.0f);
        gridFileChooserButton = new Button(BUTTON_TEXT_BROWSE_GRID);
        browseGridFileRowPanel.addComponent(gridFileChooserButton, 2, 0);
        browseGridFileRowPanel.setColumnExpandRatio(2, 0.0f);
        gridFileChooserButton.addClickListener(this);
        browseGridFileRowPanel.setComponentAlignment(gridFileCaptionLabel, Alignment.MIDDLE_LEFT);
        browseGridFileRowPanel.setComponentAlignment(gridFileChooserButton, Alignment.MIDDLE_RIGHT);

        fileContentTextArea = new TextArea();
        fileContentTextArea.setValue(content);
        fileContentTextArea.setSizeFull();

        setSpacing(true);
        setSizeFull();
        addComponent(switchRowLayout);
        addComponent(browseGridFileRowPanel);
        addComponent(fileContentTextArea);
        setExpandRatio(fileContentTextArea, 1f);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == gridFileChooserButton) {
            if (fileChooser == null) {
                synchronized (this) {
                    if (fileChooser == null) {
                        InputFilePanelCallback callback = new InputFilePanelCallback(
                                gridFilePathTextField);
                        fileChooser = new FileInStorageChooser(CAPTION_CHOOSE_GRID_FILE, callback);
                        fileChooser.addStyleName(Styles.OVERLAY_1);
                    }
                }
            }
            PortalApplication.getCurrent().addWindow(fileChooser);
        }
    }

    @Override
    public void layoutClick(LayoutClickEvent event) {
        if (event.getClickedComponent() == switchLabel) {
            boolean isBrowserOn = browseGridFileRowPanel.isVisible();
            browseGridFileRowPanel.setVisible(!isBrowserOn);
            fileContentTextArea.setEnabled(isBrowserOn);
            switchLabel.setValue(isBrowserOn ? LABEL_TOGGLE_TO_FILE_BROWSER : LABEL_TOGGLE_TO_FILE_CONTENT);
        }
    }

    public boolean isGridLocation() {
        return browseGridFileRowPanel.isVisible();
    }

    public String getFileContent() {
        return isGridLocation() ? null : fileContentTextArea.getValue();
    }

    public String getFileLocation() {
        return isGridLocation() ? (ProtocolType.BFT + ":" + gridFilePathTextField.getValue()) : null;
    }

}
