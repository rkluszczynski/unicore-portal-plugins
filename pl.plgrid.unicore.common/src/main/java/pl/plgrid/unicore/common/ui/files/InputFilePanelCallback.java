package pl.plgrid.unicore.common.ui.files;

import com.vaadin.ui.TextField;
import eu.unicore.portal.grid.core.nodes.Node;
import eu.unicore.portal.grid.ui.browser.GridStorageChooser;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.utils.FileFragmentReaderHelper;

/**
 * @author rkluszczynski
 */
public class InputFilePanelCallback implements GridStorageChooser.StorageChooserCallback {
    private final TextField textField;

    public InputFilePanelCallback(TextField textField) {
        this.textField = textField;
    }

    @Override
    public void response(Node node, boolean ok) {
        if (ok) {
            logger.info(node.getEpr().getAddress().getStringValue());
            textField.setValue(node.getEpr().getAddress().getStringValue());

            FileFragmentReaderHelper.readGridFileHead(node.getEpr(), HEAD_MAX_BYTES_COUNT);
        }
    }

    private static final long HEAD_MAX_BYTES_COUNT = 128L;

    private static final Logger logger = Logger.getLogger(InputFilePanelCallback.class);
}
