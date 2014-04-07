package pl.plgrid.unicore.common.utils;

import de.fzj.unicore.uas.client.StorageClient;
import org.unigrids.services.atomic.types.ProtocolType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Rafal
 */
public class FileDataHelper {

    public static String importFileToGrid(StorageClient storageClient, String filename, String content) throws Exception {
        InputStream source = new ByteArrayInputStream(content.getBytes());
        storageClient.getImport(filename, ProtocolType.BFT,
                ProtocolType.RBYTEIO).writeAllData(source);
        return ProtocolType.BFT + ":"
                + storageClient.getEPR().getAddress().getStringValue()
                + "#" + filename;
    }

}
