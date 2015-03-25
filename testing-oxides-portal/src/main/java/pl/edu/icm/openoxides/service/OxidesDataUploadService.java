package pl.edu.icm.openoxides.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.uas.client.TSFClient;
import de.fzj.unicore.uas.client.TSSClient;
import de.fzj.unicore.wsrflite.xmlbeans.client.RegistryClient;
import eu.unicore.security.etd.TrustDelegation;
import eu.unicore.util.httpclient.DefaultClientConfiguration;
import eu.unicore.util.httpclient.ETDClientSettings;
import eu.unicore.util.httpclient.IClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unigrids.services.atomic.types.ProtocolType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.edu.icm.openoxides.config.GridIdentityProvider;
import pl.edu.icm.openoxides.saml.ResponseDocumentWrapper;
import pl.edu.icm.openoxides.service.input.OxidesPortalData;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

@Component
public class OxidesDataUploadService {
    private final GridIdentityProvider identityProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public OxidesDataUploadService(GridIdentityProvider identityProvider, ObjectMapper objectMapper) {
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

        DefaultClientConfiguration clientConfiguration = new DefaultClientConfiguration(
                identityProvider.getGridValidator(),
                identityProvider.getGridCredential()
        );
        ETDClientSettings etdSettings = clientConfiguration.getETDSettings();
        etdSettings.setTrustDelegationTokens(Arrays.asList(trustDelegation));
        etdSettings.setRequestedUser(trustDelegation.getCustodianDN());
        etdSettings.setExtendTrustDelegation(true);

        StorageClient storageClient = extractUserHomeStorage(clientConfiguration);
        String oxidesJsonDataPath = storeDataToGridHomeStorage(storageClient, oxidesData);
        return oxidesJsonDataPath;
    }

    private StorageClient extractUserHomeStorage(IClientConfiguration clientConfiguration) throws Exception {
        String registryUrl = "https://hyx.grid.icm.edu.pl:8080/ICM-HYDRA/services/Registry?res=default_registry";
        EndpointReferenceType registryEpr = EndpointReferenceType.Factory.newInstance();
        registryEpr.addNewAddress().setStringValue(registryUrl);

        RegistryClient registryClient = new RegistryClient(registryEpr, clientConfiguration);
        QName qName = new QName("http://unigrids.org/2006/04/services/tsf", "TargetSystemFactory");
        for (EndpointReferenceType tsfEpr : registryClient.listAccessibleServices(qName)) {
//            System.err.println(" => " + tsfEpr.getAddress().getStringValue());
            TSFClient tsfClient = new TSFClient(tsfEpr, clientConfiguration);
            for (EndpointReferenceType tssEpr : tsfClient.getAccessibleTargetSystems()) {
//                System.err.println("  -> " + tssEpr.getAddress().getStringValue());
                TSSClient tssClient = new TSSClient(tssEpr, clientConfiguration);
                String targetSystemName = tssClient.getTargetSystemName();
                for (EndpointReferenceType tssStorageEpr : tssClient.getStorages()) {
                    StorageClient storageClient = new StorageClient(tssStorageEpr, clientConfiguration);
                    String storageName = storageClient.getStorageName();

//                    System.err.println("    *> " + tssStorageEpr.getAddress().getStringValue());
//                    System.err.println("     > " + storageName + " @ " + targetSystemName);

                    if ("home".equalsIgnoreCase(storageName)) {
                        return storageClient;
                    }
                }
            }
        }
//        System.out.println("DONE");
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
