package pl.edu.icm.openoxides.unicore;

import de.fzj.unicore.uas.TargetSystemFactory;
import de.fzj.unicore.uas.client.TSFClient;
import de.fzj.unicore.uas.client.TSSClient;
import de.fzj.unicore.wsrflite.xmlbeans.client.RegistryClient;
import eu.unicore.security.etd.TrustDelegation;
import eu.unicore.util.httpclient.DefaultClientConfiguration;
import eu.unicore.util.httpclient.ETDClientSettings;
import eu.unicore.util.httpclient.IClientConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.edu.icm.openoxides.config.GridIdentityProvider;
import pl.edu.icm.openoxides.saml.AuthenticationSession;
import pl.edu.icm.openoxides.saml.SamlRequestHandler;
import pl.edu.icm.openoxides.service.input.UnicoreStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TSSStorageHandler {
    private final GridIdentityProvider identityProvider;

    @Autowired
    public TSSStorageHandler(GridIdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public List<UnicoreStorage> retrieveUserStorageList(AuthenticationSession authenticationSession) {
        if (isValidAuthenticationSession(authenticationSession)) {
            IClientConfiguration userConfiguration = createUserConfiguration(authenticationSession);
            List<UnicoreStorage> storageList = collectUserStorageList(userConfiguration);
            return storageList;
        }
        return Arrays.asList(new UnicoreStorage("a"), new UnicoreStorage("b"));
    }

    public IClientConfiguration createUserConfiguration(AuthenticationSession authenticationSession) {
        TrustDelegation trustDelegation = authenticationSession.getTrustDelegations().get(0);
        DefaultClientConfiguration clientConfiguration = new DefaultClientConfiguration(
                identityProvider.getGridValidator(),
                identityProvider.getGridCredential()
        );
        ETDClientSettings etdSettings = clientConfiguration.getETDSettings();
        etdSettings.setTrustDelegationTokens(Arrays.asList(trustDelegation));
        etdSettings.setRequestedUser(trustDelegation.getCustodianDN());
        etdSettings.setExtendTrustDelegation(true);
        return clientConfiguration;
    }

    private List<UnicoreStorage> collectUserStorageList(IClientConfiguration userConfiguration) {
        String registryUrl = "https://hyx.grid.icm.edu.pl:8080/ICM-HYDRA/services/Registry?res=default_registry";
        EndpointReferenceType registryEpr = EndpointReferenceType.Factory.newInstance();
        registryEpr.addNewAddress().setStringValue(registryUrl);

        List<UnicoreStorage> unicoreStorageList = new ArrayList<>();
        try {
            RegistryClient registryClient = new RegistryClient(registryEpr, userConfiguration);
            for (EndpointReferenceType tsfEpr : registryClient.listAccessibleServices(TargetSystemFactory.TSF_PORT)) {
                TSFClient tsfClient = new TSFClient(tsfEpr, userConfiguration);
                for (EndpointReferenceType tssEpr : tsfClient.getAccessibleTargetSystems()) {
                    TSSClient tssClient = new TSSClient(tssEpr, userConfiguration);
                    unicoreStorageList.addAll(tssClient.getStorages()
                            .stream()
                            .map(tssStorageEpr -> new UnicoreStorage(tssStorageEpr.getAddress().getStringValue()))
                            .collect(Collectors.toList()));
                }
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return unicoreStorageList;
    }

    private boolean isValidAuthenticationSession(AuthenticationSession authenticationSession) {
        return authenticationSession != null
                && authenticationSession.getTrustDelegations() != null
                && authenticationSession.getTrustDelegations().size() > 0;
    }

    private Log log = LogFactory.getLog(SamlRequestHandler.class);
}
