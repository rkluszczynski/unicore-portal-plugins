package pl.plgrid.unicore.common.ui.files;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Rafal on 2015-03-12.
 */
public class GridFileUploader implements Upload.Receiver, Upload.SucceededListener {
    private final Embedded image;
    private File file;

    public GridFileUploader(Embedded image) {
        this.image = image;
    }

    public OutputStream receiveUpload(String filename,
                                      String mimeType) {
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

    public void uploadSucceeded(Upload.SucceededEvent event) {
        image.setVisible(true);
        image.setSource(new FileResource(file));
    }
}
