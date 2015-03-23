package pl.edu.icm.openoxides.controller;

import de.fzj.unicore.uas.client.TSFClient;
import de.fzj.unicore.wsrflite.xmlbeans.client.RegistryClient;
import eu.unicore.security.etd.TrustDelegation;
import eu.unicore.util.httpclient.ClientProperties;
import eu.unicore.util.httpclient.DefaultClientConfiguration;
import eu.unicore.util.httpclient.ETDClientSettings;
import eu.unicore.util.httpclient.IClientConfiguration;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import xmlbeans.org.oasis.saml2.assertion.AssertionDocument;

import javax.xml.namespace.QName;
import java.util.Arrays;

/**
 * Created by Rafal on 2015-03-23.
 */
public class OxidesHandler {
    public static void onSamlResponse(AssertionDocument assertionDocument) throws Exception {
        TrustDelegation trustDelegation;
        try {
            trustDelegation = new TrustDelegation(assertionDocument);
        } catch (Exception e) {
            System.err.println("NO ETD: " + e.getMessage());
//            e.printStackTrace(System.err);
            return;
        }

        ClientProperties jksProperties = new ClientProperties("testing-spring-boot/src/main/resources/application.properties");

        DefaultClientConfiguration clientConfiguration = jksProperties.clone();
        //new DefaultClientConfiguration();
        ETDClientSettings etdSettings = clientConfiguration.getETDSettings();
        etdSettings.setTrustDelegationTokens(Arrays.asList(trustDelegation));
        etdSettings.setRequestedUser(trustDelegation.getCustodianDN());
        etdSettings.setExtendTrustDelegation(true);

        showAccessibleTargetSystems(clientConfiguration);
    }

    private static void showAccessibleTargetSystems(IClientConfiguration clientConfiguration) throws Exception {
        String registryUrl = "https://hyx.grid.icm.edu.pl:8080/ICM-HYDRA/services/Registry?res=default_registry";
        EndpointReferenceType registryEpr = EndpointReferenceType.Factory.newInstance();
        registryEpr.addNewAddress().setStringValue(registryUrl);

        RegistryClient registryClient = new RegistryClient(registryEpr, clientConfiguration);
        QName qName = new QName("http://unigrids.org/2006/04/services/tsf", "TargetSystemFactory");
        for (EndpointReferenceType epr : registryClient.listAccessibleServices(qName)) {
            System.err.println(" -> " + epr.getAddress().getStringValue());
            TSFClient tsfClient = new TSFClient(epr, clientConfiguration);
            System.out.println(tsfClient.getResourcePropertiesDocument());
        }
        System.out.println("DONE");
    }
}
