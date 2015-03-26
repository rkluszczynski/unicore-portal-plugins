package pl.edu.icm.openoxides.saml;

import eu.unicore.security.etd.TrustDelegation;

import java.util.List;

public class AuthenticationSession {
    public static String AUTHENTICATION_SESSION_KEY = "testingAuthenticationSessionKey";

    private String idpUrl;
    private String returnUrl;
    private List<TrustDelegation> trustDelegations;

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
}
