package pl.plgrid.unicore.common.ui.nodes;

import eu.unicore.portal.grid.core.nodes.u6.FileNode;
import eu.unicore.portal.grid.ui.browser.GridStorageChooser;

/**
 * @author rkluszczynski
 */
@SuppressWarnings("serial")
public class FileInStorageChooser extends GridStorageChooser {

    public FileInStorageChooser(String caption, StorageChooserCallback callback) {
        super(caption, callback);
    }

    @Override
    public boolean isValidSelection(Object node) {
        return (node != null && node instanceof FileNode);
    }
}
