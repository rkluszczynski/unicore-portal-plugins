package pl.plgrid.unicore.common.ui.files;

import com.google.common.base.Strings;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import eu.unicore.portal.grid.ui.browser.GridFileChooser;
import eu.unicore.portal.grid.ui.browser.GridFolderChooser;
import eu.unicore.portal.ui.Styles;
import org.unigrids.services.atomic.types.ProtocolType;
import pl.plgrid.unicore.common.ui.model.GridInputFileComponent;

/**
 * @author rkluszczynski
 */
@SuppressWarnings("serial")
public class GenericInputFilePanel extends VerticalLayout implements
        GridInputFileComponent,
        Button.ClickListener,
        LayoutClickListener, Property.ValueChangeListener {
    private static final String LABEL_TOGGLE_TO_FILE_BROWSER = "<u><i>Toggle to File Browser</i></u>";
    private static final String LABEL_TOGGLE_TO_FILE_CONTENT = "<u><i>Toggle to File Content</i></u>";
    private static final String BUTTON_TEXT_BROWSE_GRID = "Browse Grid";
    private static final String BUTTON_TEXT_UPLOAD_FILE = "Upload File";
    private static final String CAPTION_CHOOSE_GRID_FILE = "Choose Grid File";
    private static final String CAPTION_GRID_FILE = " Grid File: ";

    private final GridLayout browseGridFileRowPanel;
    private final GridLayout uploadFolderPanel;
    private final GridLayout localFileUploadPanel;
    private final Label switchLabel;
    private final TextField gridFilePathTextField;
    private final TextField uploadGridFolderTextField;
    private final TextArea fileContentTextArea;
    private final Label fakePlaceHolderLabel;
    private final Button gridFileChooserButton;
    private final Button uploadFolderChooserButton;
    private Upload localFileToGridUpload;

    public GenericInputFilePanel(String content) {
        this(content, null);
    }

    public GenericInputFilePanel(String content, ObjectProperty<String> uploadFolderTextFieldProperty) {
        HorizontalLayout switchRowLayout = new HorizontalLayout();
        switchRowLayout.setWidth(100f, Unit.PERCENTAGE);
        switchRowLayout.setHeight(SIZE_UNDEFINED, Unit.EM);
        switchRowLayout.setImmediate(true);
        switchLabel = new Label(LABEL_TOGGLE_TO_FILE_BROWSER, ContentMode.HTML);
        switchLabel.setSizeUndefined();
        switchRowLayout.addComponent(switchLabel);
        switchRowLayout.setComponentAlignment(switchLabel, Alignment.MIDDLE_RIGHT);
        switchRowLayout.addLayoutClickListener(this);

        gridFilePathTextField = new TextField();
        gridFileChooserButton = new Button(BUTTON_TEXT_BROWSE_GRID);
        browseGridFileRowPanel = createBrowseGridFilePanel();

        uploadGridFolderTextField = new TextField();
        if (uploadFolderTextFieldProperty != null) {
            uploadGridFolderTextField.setPropertyDataSource(uploadFolderTextFieldProperty);
        }
        uploadGridFolderTextField.setImmediate(true);
        uploadGridFolderTextField.setSizeFull();
        uploadGridFolderTextField.setVisible(true);
        uploadGridFolderTextField.setEnabled(false);
        uploadGridFolderTextField.addValueChangeListener(this);
        uploadFolderChooserButton = new Button("Select Grid Folder");
        uploadFolderPanel = createUploadFolderPanel();

        localFileUploadPanel = createLocalFileUploadPanel();

        fileContentTextArea = new TextArea();
        fileContentTextArea.setValue(content);
        fileContentTextArea.setSizeFull();

        fakePlaceHolderLabel = new Label("");
        fakePlaceHolderLabel.setEnabled(false);
        fakePlaceHolderLabel.setVisible(false);
        fakePlaceHolderLabel.setSizeUndefined();

        setSpacing(true);
        setSizeFull();
        addComponent(switchRowLayout);
        addComponent(browseGridFileRowPanel);
        addComponent(uploadFolderPanel);
        addComponent(localFileUploadPanel);
        addComponent(fileContentTextArea);
        addComponent(fakePlaceHolderLabel);
        setExpandRatio(fileContentTextArea, 1f);
        setExpandRatio(fakePlaceHolderLabel, 0.9f);
    }

    private GridLayout createLocalFileUploadPanel() {
        GridLayout localFileUploadPanel = createNewGridLayoutPanel(2);

        Label statusLabel = new Label("", ContentMode.HTML);
        statusLabel.setSizeUndefined();

        GridFileUploadReceiver receiver = new GridFileUploadReceiver(statusLabel);

        localFileToGridUpload = new Upload(null, receiver);
        localFileToGridUpload.addStartedListener(receiver);
        localFileToGridUpload.addSucceededListener(receiver);
        localFileToGridUpload.addFailedListener(receiver);
        localFileToGridUpload.setButtonCaption("Start Upload");
        localFileToGridUpload.setWidth(100f, Unit.PERCENTAGE);
        localFileToGridUpload.setHeight(SIZE_UNDEFINED, Unit.EM);
        localFileToGridUpload.setSizeFull();

        localFileUploadPanel.addComponent(localFileToGridUpload, 0, 0);
        localFileUploadPanel.setColumnExpandRatio(0, 1.0f);
        localFileUploadPanel.addComponent(statusLabel, 1, 0);
        localFileUploadPanel.setColumnExpandRatio(1, 0.0f);

        localFileUploadPanel.setComponentAlignment(localFileToGridUpload, Alignment.MIDDLE_LEFT);
        localFileUploadPanel.setComponentAlignment(statusLabel, Alignment.MIDDLE_RIGHT);

        return localFileUploadPanel;
    }

    private GridLayout createUploadFolderPanel() {
        GridLayout uploadFolderRowPanel = createNewGridLayoutPanel(3);

        Label uploadFolderCaptionLabel = new Label("Upload Grid Folder: ");
        uploadFolderCaptionLabel.setSizeUndefined();
        uploadFolderRowPanel.addComponent(uploadFolderCaptionLabel, 0, 0);
        uploadFolderRowPanel.setColumnExpandRatio(0, 0.0f);
        uploadFolderRowPanel.addComponent(uploadGridFolderTextField, 1, 0);
        uploadFolderRowPanel.setColumnExpandRatio(1, 1.0f);
        uploadFolderRowPanel.addComponent(uploadFolderChooserButton, 2, 0);
        uploadFolderRowPanel.setColumnExpandRatio(2, 0.0f);
        uploadFolderChooserButton.addClickListener(this);
        uploadFolderRowPanel.setComponentAlignment(uploadFolderCaptionLabel, Alignment.MIDDLE_LEFT);
        uploadFolderRowPanel.setComponentAlignment(uploadFolderChooserButton, Alignment.MIDDLE_RIGHT);

        return uploadFolderRowPanel;
    }

    private GridLayout createBrowseGridFilePanel() {
        GridLayout gridFileRowPanel = createNewGridLayoutPanel(3);

        Label gridFileCaptionLabel = new Label(CAPTION_GRID_FILE);
        gridFileCaptionLabel.setSizeUndefined();
        gridFileRowPanel.addComponent(gridFileCaptionLabel, 0, 0);
        gridFileRowPanel.setColumnExpandRatio(0, 0.0f);
        gridFilePathTextField.setSizeFull();
        gridFilePathTextField.setEnabled(false);
        gridFileRowPanel.addComponent(gridFilePathTextField, 1, 0);
        gridFileRowPanel.setColumnExpandRatio(1, 1.0f);
        gridFileRowPanel.addComponent(gridFileChooserButton, 2, 0);
        gridFileRowPanel.setColumnExpandRatio(2, 0.0f);
        gridFileChooserButton.addClickListener(this);
        gridFileRowPanel.setComponentAlignment(gridFileCaptionLabel, Alignment.MIDDLE_LEFT);
        gridFileRowPanel.setComponentAlignment(gridFileChooserButton, Alignment.MIDDLE_RIGHT);
        return gridFileRowPanel;
    }

    private GridLayout createNewGridLayoutPanel(int columns) {
        GridLayout gridFileRowPanel = new GridLayout(columns, 1);
        gridFileRowPanel.setWidth(100f, Unit.PERCENTAGE);
        gridFileRowPanel.setHeight(SIZE_UNDEFINED, Unit.EM);
        gridFileRowPanel.setVisible(false);
        gridFileRowPanel.setSpacing(true);
        return gridFileRowPanel;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == gridFileChooserButton) {
            GridFileChooser fileChooser = new GridFileChooser(CAPTION_CHOOSE_GRID_FILE,
                    new InputFilePanelCallback(
                            gridFilePathTextField
                    )
            );
            fileChooser.addStyleName(Styles.OVERLAY_1);
            fileChooser.setModal(true);
//            PortalApplication.getCurrent().addWindow(fileChooser);
            UI.getCurrent().addWindow(fileChooser);
        } else if (event.getButton() == uploadFolderChooserButton) {
            GridFolderChooser folderChooser = new GridFolderChooser("Choose folder for uploads",
                    new UploadFolderPanelCallback(
                            uploadGridFolderTextField, localFileUploadPanel
                    )
            );
            folderChooser.addStyleName(Styles.OVERLAY_1);
            folderChooser.setModal(true);
            UI.getCurrent().addWindow(folderChooser);
        }
    }

    @Override
    public void layoutClick(LayoutClickEvent event) {
        if (event.getClickedComponent() == switchLabel) {
            boolean isBrowserOn = browseGridFileRowPanel.isVisible();
            browseGridFileRowPanel.setVisible(!isBrowserOn);
            uploadFolderPanel.setVisible(!isBrowserOn);
            localFileUploadPanel.setVisible(!isBrowserOn);
            setLocalFileUploadVisibility();
            fileContentTextArea.setEnabled(isBrowserOn);
            fileContentTextArea.setVisible(isBrowserOn);
            fakePlaceHolderLabel.setVisible(!isBrowserOn);
            switchLabel.setValue(isBrowserOn ? LABEL_TOGGLE_TO_FILE_BROWSER : LABEL_TOGGLE_TO_FILE_CONTENT);
        }
    }

    private boolean isGridLocation() {
        return browseGridFileRowPanel.isVisible();
    }

    private String getFileContent() {
        return isGridLocation() ? null : fileContentTextArea.getValue();
    }

    private String getFileLocation() {
        // TODO: fixed protocol type to BFT
        return isGridLocation() ? (ProtocolType.BFT + ":" + gridFilePathTextField.getValue()) : null;
    }

    @Override
    public GridInputFileData getInputFileData() {
        if (isGridLocation()) {
            return new GridInputFileData(
                    GridInputFileValueType.VALUE_GRID_PATH,
                    getFileLocation()
            );
        } else {
            return new GridInputFileData(
                    GridInputFileValueType.VALUE_CONTENT,
                    getFileContent()
            );
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        setLocalFileUploadVisibility();
    }

    private void setLocalFileUploadVisibility() {
        localFileToGridUpload.setEnabled(!Strings.isNullOrEmpty(uploadGridFolderTextField.getValue()));
    }
}
