package pl.plgrid.unicore.common.ui.files;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import eu.unicore.portal.grid.core.nodes.Node;
import eu.unicore.portal.grid.ui.browser.GridStorageChooser;
import org.apache.log4j.Logger;

/**
 * @author rkluszczynski
 */
public class UploadFolderPanelCallback implements GridStorageChooser.StorageChooserCallback {
    private final TextField textField;
    private final GridLayout layout;

    public UploadFolderPanelCallback(TextField textField, GridLayout layout) {
        this.textField = textField;
        this.layout = layout;
    }

    @Override
    public void response(Node node, boolean ok) {
        if (ok) {
            logger.info(node.getEpr().getAddress().getStringValue());
            textField.setValue(node.getEpr().getAddress().getStringValue());

            layout.setEnabled(true);
        }
    }

    private static final Logger logger = Logger.getLogger(UploadFolderPanelCallback.class);
}
