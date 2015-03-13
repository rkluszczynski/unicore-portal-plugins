package pl.plgrid.unicore.common.ui.files;

import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Rafal on 2015-03-12.
 */
public class GridFileUploadReceiver implements
        Upload.StartedListener,
        Upload.Receiver,
        Upload.SucceededListener,
        Upload.FailedListener {
    private static final Logger logger = Logger.getLogger(GridFileUploadReceiver.class);
    private final Label label;
    private File file;

    public GridFileUploadReceiver(Label label) {
        this.label = label;
    }

    @Override
    public void uploadStarted(Upload.StartedEvent event) {
        setLabelValueWithColor("uploading", "black");
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        FileOutputStream fos = null; // Stream to write to
        try {
            // Open the file for writing.
            file = new File("/tmp/uploads/" + filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            new Notification("Could not open file<br/>",
                    e.getMessage(),
                    Notification.Type.ERROR_MESSAGE)
                    .show(Page.getCurrent());
            return null;
        }
        return fos; // Return the output stream to write to
    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent event) {
        setLabelValueWithColor("file uploaded", "darkgreen");
    }

    @Override
    public void uploadFailed(Upload.FailedEvent event) {
        setLabelValueWithColor("upload failed", "darkred");
    }

    private void setLabelValueWithColor(String message, String color) {
        String value = String.format("<i style=\"color:%s\">%s</i>", color, message);
        label.setValue(value);
    }
}
