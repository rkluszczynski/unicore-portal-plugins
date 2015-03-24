package pl.edu.icm.openoxides.app

import de.fzj.unicore.wsrflite.xmlbeans.client.RegistryClient
import eu.emi.security.authn.x509.impl.KeystoreCredential
import eu.unicore.util.httpclient.ClientProperties
import org.w3.x2005.x08.addressing.EndpointReferenceType

import javax.xml.namespace.QName

/**
 * Created by Rafal on 2015-03-24.
 */
class GridCredentialTestApp {

    public static void main(String[] args) {
        KeystoreCredential credential = new KeystoreCredential(
                "src/main/resources/oxides.jks",
                "oxides".toCharArray(),
                "oxides".toCharArray(),
                "oxides",
                "JKS"
        );
        System.out.println(credential.getCertificate().getSubjectX500Principal().toString());
        System.out.println(credential.getSubjectName());

        String registryUrl = "https://hyx.grid.icm.edu.pl:8080/ICM-HYDRA/services/Registry?res=default_registry";
        EndpointReferenceType registryEpr = EndpointReferenceType.Factory.newInstance();
        registryEpr.addNewAddress().setStringValue(registryUrl);

        ClientProperties clientProperties = new ClientProperties("src/main/resources/application.properties")
        RegistryClient registryClient = new RegistryClient(registryEpr, clientProperties);

        QName qName = new QName("http://unigrids.org/2006/04/services/tsf", "TargetSystemFactory");
        for (EndpointReferenceType epr : registryClient.listAccessibleServices(qName)) {
            System.out.println(" -> " + epr.getAddress().getStringValue());
        }
        System.out.println("DONE");
    }
}
