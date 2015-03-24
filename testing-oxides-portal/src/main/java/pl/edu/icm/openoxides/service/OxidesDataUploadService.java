package pl.edu.icm.openoxides.service;

import de.fzj.unicore.uas.client.TSFClient;
import de.fzj.unicore.uas.client.TSSClient;
import de.fzj.unicore.wsrflite.xmlbeans.client.RegistryClient;
import eu.unicore.security.etd.TrustDelegation;
import eu.unicore.util.httpclient.DefaultClientConfiguration;
import eu.unicore.util.httpclient.ETDClientSettings;
import eu.unicore.util.httpclient.IClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.edu.icm.openoxides.config.GridIdentityProvider;
import pl.edu.icm.openoxides.saml.ResponseDocumentWrapper;

import javax.xml.namespace.QName;
import java.util.Arrays;

@Component
public class OxidesDataUploadService {
    private final GridIdentityProvider identityProvider;

    @Autowired
    public OxidesDataUploadService(GridIdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public void onSamlResponse(ResponseDocumentWrapper documentWrapper) throws Exception {
        TrustDelegation trustDelegation;
        try {
            trustDelegation = new TrustDelegation(documentWrapper.getEtdAssertions().get(0));
        } catch (Exception e) {
            System.err.println("NO ETD: " + e.getMessage());
//            e.printStackTrace(System.err);
            return;
        }

        DefaultClientConfiguration clientConfiguration = new DefaultClientConfiguration(
                identityProvider.getGridValidator(),
                identityProvider.getGridCredential()
        );
        ETDClientSettings etdSettings = clientConfiguration.getETDSettings();
        etdSettings.setTrustDelegationTokens(Arrays.asList(trustDelegation));
        etdSettings.setRequestedUser(trustDelegation.getCustodianDN());
        etdSettings.setExtendTrustDelegation(true);

        showAccessibleTargetSystems(clientConfiguration);
    }

    private void showAccessibleTargetSystems(IClientConfiguration clientConfiguration) throws Exception {
        String registryUrl = "https://hyx.grid.icm.edu.pl:8080/ICM-HYDRA/services/Registry?res=default_registry";
        EndpointReferenceType registryEpr = EndpointReferenceType.Factory.newInstance();
        registryEpr.addNewAddress().setStringValue(registryUrl);

        RegistryClient registryClient = new RegistryClient(registryEpr, clientConfiguration);
        QName qName = new QName("http://unigrids.org/2006/04/services/tsf", "TargetSystemFactory");
        for (EndpointReferenceType tsfEpr : registryClient.listAccessibleServices(qName)) {
            System.err.println(" => " + tsfEpr.getAddress().getStringValue());
            TSFClient tsfClient = new TSFClient(tsfEpr, clientConfiguration);
            for (EndpointReferenceType tssEpr : tsfClient.getAccessibleTargetSystems()) {
                System.err.println("  -> " + tssEpr.getAddress().getStringValue());
                TSSClient tssClient = new TSSClient(tssEpr, clientConfiguration);
                for (EndpointReferenceType tssStorageEpr : tssClient.getStorages()) {
                    System.err.println("    *> " + tssStorageEpr.getAddress().getStringValue());
                }
            }
        }
        System.out.println("DONE");
    }
}
