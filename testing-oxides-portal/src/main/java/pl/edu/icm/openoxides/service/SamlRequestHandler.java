package pl.edu.icm.openoxides.service;

import eu.emi.security.authn.x509.impl.KeystoreCredential;
import eu.unicore.samly2.SAMLConstants;
import eu.unicore.samly2.binding.HttpPostBindingSupport;
import eu.unicore.samly2.binding.SAMLMessageType;
import eu.unicore.samly2.elements.NameID;
import eu.unicore.samly2.proto.AuthnRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import xmlbeans.org.oasis.saml2.protocol.AuthnRequestDocument;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URI;

@Component
public class SamlRequestHandler {
    public final String idpUrl = "https://unity.grid.icm.edu.pl/testbed-portal/saml2unicoreIdp-web";
    //    public final String targetUrl = "https://alfred.mat.umk.pl:8443/authn/saml";
    public final String targetUrl = "https://localhost:8443/authn/saml";

    public void performAuthenticationRequest(HttpServletResponse response) {
        try {
            AuthnRequest authnRequest = createRequest(idpUrl, targetUrl);
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
            e.printStackTrace(System.err);
        }
    }

    private void configureHttpResponse(HttpServletResponse response) {
        response.setContentType(String.format("%s; charset=utf-8", MediaType.TEXT_HTML));
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache,no-store,must-revalidate");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, -1);
    }

    private AuthnRequest createRequest(String idpUrl, String targetUrl) throws Exception {
        URI samlServletUri = new URI(targetUrl);
        KeystoreCredential credential = new KeystoreCredential(
                "src/main/resources/oxides.jks",
                "oxides".toCharArray(),
                "oxides".toCharArray(),
                "oxides",
                "JKS"
        );
        NameID myId = new NameID(credential.getSubjectName(), SAMLConstants.NFORMAT_DN);

        AuthnRequest request = new AuthnRequest(myId.getXBean());
        request.setFormat(SAMLConstants.NFORMAT_DN);
        request.getXMLBean().setDestination(idpUrl);
        request.getXMLBean().setAssertionConsumerServiceURL(samlServletUri.toASCIIString());

        request.sign(credential.getKey(), credential.getCertificateChain());
        return request;
    }
}
