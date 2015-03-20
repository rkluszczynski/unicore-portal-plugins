package pl.edu.icm.openoxides.controller;

import java.io.Serializable;
import java.util.Date;

/**
 * Stored in session. Used to pass information about SAML authentication parameters from
 * Vaadin portal app to SAML service provider servlet: {@link SAMLAuthnServlet}.
 *
 * @author K. Benedyczak
 */
public class SamlAuthenticationContext implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String SESSION_KEY = SamlAuthenticationContext.class.getName();
    private String idpUrl;
    private String samlRequest;
    private String returnUrl;
    private String requestId;
    private Date ts;
    private String componentId;
    private String samlResponse;

    public SamlAuthenticationContext(String idpUrl, String samlRequest, String returnUrl, String requestId,
                                     String componentId) {
        this.idpUrl = idpUrl;
        this.samlRequest = samlRequest;
        this.returnUrl = returnUrl;
        this.requestId = requestId;
        this.ts = new Date();
        this.componentId = componentId;
    }


    public String getSamlResponse() {
        return samlResponse;
    }

    public void setSamlResponse(String samlResponse) {
        this.samlResponse = samlResponse;
    }

    public String getIdpUrl() {
        return idpUrl;
    }

    public void setIdpUrl(String idpUrl) {
        this.idpUrl = idpUrl;
    }

    public String getSamlRequest() {
        return samlRequest;
    }

    public void setSamlRequest(String samlRequest) {
        this.samlRequest = samlRequest;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public String getComponentId() {
        return componentId;
    }
}
