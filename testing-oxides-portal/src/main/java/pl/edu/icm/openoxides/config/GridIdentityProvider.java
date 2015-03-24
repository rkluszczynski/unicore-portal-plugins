package pl.edu.icm.openoxides.config;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.X509Credential;
import eu.unicore.security.canl.TrustedIssuersProperties;
import eu.unicore.util.httpclient.ClientProperties;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
public class GridIdentityProvider {
    private final X509Credential gridCredential;
    private final X509CertChainValidatorExt gridValidator;
    private final X509CertChainValidatorExt idpValidator;

    public GridIdentityProvider() throws IOException {
        String applicationPropertiesPath = "src/main/resources/application.properties";

        ClientProperties clientProperties = new ClientProperties(applicationPropertiesPath);
        gridCredential = clientProperties.getAuthnAndTrustConfiguration().getCredential();
        gridValidator = clientProperties.getAuthnAndTrustConfiguration().getValidator();

        Properties applicationProperties = new Properties();
        applicationProperties.load(new FileInputStream(applicationPropertiesPath));
        TrustedIssuersProperties trustedIssuersProperties = new TrustedIssuersProperties(
                applicationProperties, null, "idp.truststore."
        );
        idpValidator = trustedIssuersProperties.getValidator();
    }

    public X509Credential getGridCredential() {
        return gridCredential;
    }

    public X509CertChainValidatorExt getGridValidator() {
        return gridValidator;
    }

    public X509CertChainValidatorExt getIdpValidator() {
        return idpValidator;
    }
}