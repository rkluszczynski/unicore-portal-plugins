package pl.edu.icm.openoxides.saml;

import eu.unicore.security.etd.TrustDelegation;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
//@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
//@Lazy
public class AuthenticationSession {
    public static String AUTHENTICATION_SESSION_KEY = "testingAuthenticationSessionKey";

    private String idpUrl;
    private String returnUrl;
    private List<TrustDelegation> trustDelegations;

    private String uuid = UUID.randomUUID().toString();

    public String getIdpUrl() {
        return idpUrl;
    }

    public void setIdpUrl(String idpUrl) {
        this.idpUrl = idpUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public List<TrustDelegation> getTrustDelegations() {
        return trustDelegations;
    }

    public void setTrustDelegations(List<TrustDelegation> trustDelegations) {
        this.trustDelegations = trustDelegations;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
