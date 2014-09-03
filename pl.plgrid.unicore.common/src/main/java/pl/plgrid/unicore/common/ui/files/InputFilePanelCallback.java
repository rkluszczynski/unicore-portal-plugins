package pl.plgrid.unicore.common.ui.files;

import com.vaadin.ui.TextField;
import eu.unicore.portal.grid.core.nodes.Node;
import eu.unicore.portal.grid.ui.browser.GridStorageChooser;
import org.apache.log4j.Logger;

/**
 * @author rkluszczynski
 */
public class InputFilePanelCallback implements
        GridStorageChooser.StorageChooserCallback {

    private static final Logger logger = Logger
            .getLogger(InputFilePanelCallback.class);

    private final TextField textField;

    public InputFilePanelCallback(TextField textField) {
        this.textField = textField;
    }

    @Override
    public void response(Node node, boolean ok) {
        logger.info("endpoint = " + node.getNamePathToRoot() + ", name = "
                + node.getLowLevelAsString() + ", ok = " + ok);
        if (ok) {
            logger.info(node.getEpr().getAddress().getStringValue());
            textField.setValue(node.getEpr().getAddress().getStringValue());
        }
    }
}
