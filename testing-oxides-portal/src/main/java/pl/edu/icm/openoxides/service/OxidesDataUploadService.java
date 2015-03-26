package pl.edu.icm.openoxides.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fzj.unicore.uas.client.StorageClient;
import eu.unicore.security.etd.TrustDelegation;
import eu.unicore.util.httpclient.IClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unigrids.services.atomic.types.ProtocolType;
import pl.edu.icm.openoxides.config.GridIdentityProvider;
import pl.edu.icm.openoxides.saml.AuthenticationSession;
import pl.edu.icm.openoxides.saml.ResponseDocumentWrapper;
import pl.edu.icm.openoxides.service.input.OxidesPortalData;
import pl.edu.icm.openoxides.service.input.UnicoreStorage;
import pl.edu.icm.openoxides.unicore.TSSStorageHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class OxidesDataUploadService {
    private final TSSStorageHandler tssStorageHandler;
    private final GridIdentityProvider identityProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public OxidesDataUploadService(TSSStorageHandler tssStorageHandler, GridIdentityProvider identityProvider, ObjectMapper objectMapper) {
        this.tssStorageHandler = tssStorageHandler;
        this.identityProvider = identityProvider;
        this.objectMapper = objectMapper;
    }

    public String onSamlResponse(ResponseDocumentWrapper documentWrapper, OxidesPortalData oxidesData) throws Exception {
        TrustDelegation trustDelegation;
        try {
            trustDelegation = new TrustDelegation(documentWrapper.getEtdAssertions().get(0));
        } catch (Exception e) {
            System.err.println("NO ETD: " + e.getMessage());
//            e.printStackTrace(System.err);
            return "ERROR: " + e.getMessage();
        }

        AuthenticationSession authenticationSession = new AuthenticationSession();
        authenticationSession.setTrustDelegations(Arrays.asList(trustDelegation));

        StorageClient storageClient = extractUserHomeStorage(
                tssStorageHandler.retrieveUserStorageList(authenticationSession),
                tssStorageHandler.createUserConfiguration(authenticationSession)
        );
        String oxidesJsonDataPath = storeDataToGridHomeStorage(storageClient, oxidesData);
        return oxidesJsonDataPath;
    }

    private StorageClient extractUserHomeStorage(List<UnicoreStorage> unicoreStorageList, IClientConfiguration userConfiguration) throws Exception {
        for (UnicoreStorage unicoreStorage : unicoreStorageList) {
            StorageClient storageClient = new StorageClient(unicoreStorage.getEpr(), userConfiguration);
            String storageName = storageClient.getStorageName();
//                    System.err.println("    *> " + tssStorageEpr.getAddress().getStringValue());
//                    System.err.println("     > " + storageName + " @ " + targetSystemName);
            if ("home".equalsIgnoreCase(storageName)) {
                return storageClient;
            }
        }
        return null;
    }

    private String storeDataToGridHomeStorage(StorageClient storageClient, OxidesPortalData oxidesData) throws Exception {
        String oxidesJson = objectMapper.writeValueAsString(oxidesData);
        InputStream source = new ByteArrayInputStream(oxidesJson.getBytes());
        storageClient.getImport(OXIDES_DATA_TARGET_JSON_FILE, ProtocolType.BFT).writeAllData(source);
        return String.format("%s:%s#%s", ProtocolType.BFT,
                storageClient.getEPR().getAddress().getStringValue(), OXIDES_DATA_TARGET_JSON_FILE);
    }

    private static final String OXIDES_DATA_TARGET_JSON_FILE = "oxides.json";
    public static final String OXIDES_JSON_SESSION_ATTRIBUTE_KEY = "oxides.json";
}
