package pl.edu.icm.openoxides.saml;

import eu.emi.security.authn.x509.X509Credential;
import eu.unicore.samly2.SAMLConstants;
import eu.unicore.samly2.binding.HttpPostBindingSupport;
import eu.unicore.samly2.binding.SAMLMessageType;
import eu.unicore.samly2.elements.NameID;
import eu.unicore.samly2.proto.AuthnRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import pl.edu.icm.openoxides.config.GridIdentityProvider;
import xmlbeans.org.oasis.saml2.protocol.AuthnRequestDocument;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URI;

@Component
public class SamlRequestHandler {
    public static final String idpUrl = "https://unity.grid.icm.edu.pl/testbed-portal/saml2unicoreIdp-web";
    //    public static final String targetUrl = "https://alfred.mat.umk.pl:8443/authn/saml";
    public static final String targetUrl = "https://localhost:8443/authn/saml";
    public static final String requestId = "testingOpenOxides";

    private final GridIdentityProvider identityProvider;

    @Autowired
    public SamlRequestHandler(GridIdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public void performAuthenticationRequest(HttpServletResponse response, AuthenticationSession authenticationSession) {
        try {
            AuthnRequest authnRequest = createRequest(idpUrl, targetUrl, identityProvider.getGridCredential());
            AuthnRequestDocument authnRequestDocument = AuthnRequestDocument.Factory.parse(
                    authnRequest.getXMLBeanDoc().xmlText());

            configureHttpResponse(response);
            String form = HttpPostBindingSupport.getHtmlPOSTFormContents(
                    SAMLMessageType.SAMLRequest,
                    idpUrl,
                    authnRequestDocument.xmlText(),
                    null);
            PrintWriter writer = response.getWriter();
            writer.write(form);
            writer.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void configureHttpResponse(HttpServletResponse response) {
        response.setContentType(String.format("%s; charset=utf-8", MediaType.TEXT_HTML));
//        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache,no-store,must-revalidate");
//        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
//        response.setDateHeader(HttpHeaders.EXPIRES, -1);
    }

    private AuthnRequest createRequest(String idpUrl, String targetUrl, X509Credential credential) throws Exception {
        URI samlServletUri = new URI(targetUrl);
        NameID myId = new NameID(credential.getSubjectName(), SAMLConstants.NFORMAT_DN);

        AuthnRequest request = new AuthnRequest(myId.getXBean());
        request.setFormat(SAMLConstants.NFORMAT_DN);
        request.getXMLBean().setDestination(idpUrl);
        request.getXMLBean().setAssertionConsumerServiceURL(samlServletUri.toASCIIString());

        // TODO: comment out line below to use autogenerated id (remember to store it in session)
        request.getXMLBean().setID(requestId);

        request.sign(credential.getKey(), credential.getCertificateChain());
        return request;
    }

    private Log log = LogFactory.getLog(SamlRequestHandler.class);
}
