package pl.plgrid.unicore.common.utils;

import com.google.common.collect.Maps;
import de.fzj.unicore.uas.client.FileTransferClient;
import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.uas.fts.FiletransferOptions;
import org.apache.log4j.Logger;
import org.unigrids.services.atomic.types.ProtocolType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @author R.Kluszczynski
 */
public final class FileDataHelper {

    public static String importFileToGrid(StorageClient storageClient, String filename, String content) throws Exception {
        InputStream source = new ByteArrayInputStream(content.getBytes());
        storageClient.getImport(filename, ProtocolType.BFT,
                ProtocolType.RBYTEIO).writeAllData(source);
        return ProtocolType.BFT + ":"
                + storageClient.getEPR().getAddress().getStringValue()
                + "#" + filename;
    }

    public static String readGridFileHead(EndpointReferenceType epr, long bytes) {
        return readGridFileFragment(epr, 0L, bytes);
    }

    /**
     * Assuming that hardcoded BFT always support partial read.
     *
     * @param gridFileEpr
     * @param startByte
     * @param endByte
     * @return
     */
    private static String readGridFileFragment(EndpointReferenceType gridFileEpr, long startByte, long endByte) {
        String gridFileUrl = gridFileEpr.getAddress().getStringValue();

        EndpointReferenceType storageEpr = EndpointReferenceType.Factory.newInstance();
        storageEpr.addNewAddress().setStringValue(gridFileUrl.substring(0, gridFileUrl.indexOf("#")));
        String gridFilePath = gridFileUrl.substring(gridFileUrl.indexOf("#") + 1);
        logger.debug(String.format("Contacting storage <%s> for reading fragment of file <%s>",
                storageEpr.getAddress().getStringValue(), gridFilePath));

        FileTransferClient transferClient;
        try {
            StorageClient client = new StorageClient(storageEpr, SecurityHelper.getClientConfig());
            logger.debug("SMS contacted, server reply: serverTime=" + client.getCurrentTime());

            HashMap<String, String> extraParameters = Maps.newHashMap();
            transferClient = client.getExport(gridFilePath, extraParameters, ProtocolType.BFT);
            logger.debug("Created file transfer: " + transferClient.getUrl());
        } catch (IOException e) {
            logger.error(String.format("Could not initialize transfer for file <%s>", gridFilePath), e);
            return null;
        } catch (Exception e) {
            logger.error(String.format("Unable to create storage client for EPR = %s",
                    storageEpr.getAddress().getStringValue()), e);
            return null;
        }

        FiletransferOptions.SupportsPartialRead partialRead = (FiletransferOptions.SupportsPartialRead) transferClient;
        String readFileFragmentString;
        try {
            ByteArrayOutputStream targetStream = new ByteArrayOutputStream();
            partialRead.readPartial(startByte, endByte - startByte + 1, targetStream);
            readFileFragmentString = new String(targetStream.toByteArray(), "UTF-8");

            logger.info(String.format("READ: %s", readFileFragmentString));
            return readFileFragmentString;
        } catch (IOException e) {
            logger.error(String.format("Could not read fragment of file <%s>",
                    gridFileEpr.getAddress().getStringValue()), e);
        }
        return null;
    }

    private FileDataHelper() {
    }

    private static final Logger logger = Logger.getLogger(FileDataHelper.class);
}
