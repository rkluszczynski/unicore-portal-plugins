package pl.plgrid.unicore.common.ui.files;

import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import de.fzj.unicore.uas.client.StorageClient;
import eu.unicore.portal.core.threads.BackgroundWorker;
import eu.unicore.portal.core.threads.IProgressMonitor;
import org.apache.log4j.Logger;
import org.unigrids.services.atomic.types.ProtocolType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;

import java.io.*;

/**
 * Created by Rafal on 2015-03-12.
 */
public class GridFileUploadReceiver implements
        Upload.StartedListener,
        Upload.Receiver,
        Upload.SucceededListener,
        Upload.FailedListener,
        Upload.FinishedListener {
    private static final Logger logger = Logger.getLogger(GridFileUploadReceiver.class);

    private final TextField targetTextField;
    private final TextField addressTextField;
    private final Label statusLabel;

    private String lastFilename;

    public GridFileUploadReceiver(TextField targetTextField, TextField addressTextField, Label statusLabel) {
        this.targetTextField = targetTextField;
        this.addressTextField = addressTextField;
        this.statusLabel = statusLabel;
    }

    @Override
    public void uploadStarted(Upload.StartedEvent event) {
        setLabelValueWithColor("uploading", "black");
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        return getGridOutputStream(filename);
    }

    private OutputStream getGridOutputStream(String filename) {
        lastFilename = filename;
        String address = targetTextField.getValue();
        int hashDelimiter = address.indexOf("#");

        EndpointReferenceType storageEpr = EndpointReferenceType.Factory.newInstance();
        storageEpr.addNewAddress().setStringValue(hashDelimiter < 0 ? address : address.substring(0, hashDelimiter));

        final String fileRelativePath = hashDelimiter < 0 ? "" : address.substring(hashDelimiter + 1)
                + String.format("/%s", filename);

        final PipedInputStream in = new PipedInputStream();
        final EndpointReferenceType smsEpr = storageEpr;
        new BackgroundWorker(String.format("Uploading file %s", filename)) {
            @Override
            protected void work(IProgressMonitor progress) {
                try {
                    new StorageClient(smsEpr, SecurityHelper.getClientConfig())
                            .getImport(fileRelativePath, ProtocolType.BFT, ProtocolType.RBYTEIO)
                            .writeAllData(in);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }.schedule();

        try {
            return new PipedOutputStream(in);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private OutputStream getFileOutputStream(String filename) {
        FileOutputStream fos = null; // Stream to write to
        try {
            // Open the file for writing.
            File file = new File("/tmp/uploads/" + filename);
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
        setLabelValueWithColor("file uploaded", "green");
        addressTextField.setValue(String.format("%s/%s", targetTextField.getValue(), lastFilename));
    }

    @Override
    public void uploadFailed(Upload.FailedEvent event) {
        setLabelValueWithColor("upload failed", "darkred");
    }

    @Override
    public void uploadFinished(Upload.FinishedEvent event) {
        lastFilename = null;
    }

    private void setLabelValueWithColor(String message, String color) {
        String value = String.format("<i style=\"color:%s\">%s</i>", color, message);
        statusLabel.setValue(value);
    }
}
