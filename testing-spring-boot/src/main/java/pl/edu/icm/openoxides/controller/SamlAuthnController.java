package pl.edu.icm.openoxides.controller;

import eu.emi.security.authn.x509.impl.KeystoreCredential;
import eu.unicore.samly2.SAMLConstants;
import eu.unicore.samly2.binding.HttpPostBindingSupport;
import eu.unicore.samly2.binding.SAMLMessageType;
import eu.unicore.samly2.elements.NameID;
import eu.unicore.samly2.proto.AuthnRequest;
import eu.unicore.security.dsig.DSigException;
import org.apache.xmlbeans.XmlException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xmlbeans.org.oasis.saml2.protocol.AuthnRequestDocument;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStoreException;

@RestController
public class SamlAuthnController {
    public static final String IDP_URL = "https://unity.grid.icm.edu.pl/unicore-portal/saml2unicoreIdp-web";
    public static final String DISTINGUISH_NAME_ID = "CN=portal,O=GRID,C=PL";
    private SamlAuthenticationContext authenticationContext;

    public SamlAuthnController() throws Exception {
        AuthnRequest request = createRequest();

        authenticationContext = new SamlAuthenticationContext(IDP_URL,
                request.getXMLBeanDoc().xmlText(),
                "http://wp.pl",
                request.getXMLBean().getID(),
                getUniqueIdentifier()
        );
    }

    @RequestMapping("/authn")
    public void authnWithUnity(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(SamlAuthenticationContext.SESSION_KEY, authenticationContext);

        AuthnRequestDocument reqDoc;
        try {
            reqDoc = AuthnRequestDocument.Factory.parse(authenticationContext.getSamlRequest());
        } catch (XmlException e) {
            response.sendRedirect("http://localhost:8080");
            return;
        }
        configureHttpResponse(response);

        String form = HttpPostBindingSupport.getHtmlPOSTFormContents(
                SAMLMessageType.SAMLRequest,
                authenticationContext.getIdpUrl(),
                reqDoc.xmlText(),
                null);
        PrintWriter writer = response.getWriter();
        writer.write(form);
        writer.flush();

//        response.sendRedirect(IDP_URL);
    }

    private void configureHttpResponse(HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("Cache-Control", "no-cache,no-store,must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);
    }

    private String getUniqueIdentifier() {
        return "UUID";
    }

    private AuthnRequest createRequest() throws DSigException, IOException, URISyntaxException, KeyStoreException {
        URI samlServletUri = new URI("https://unicore.studmat.umk.pl:9091/authn");

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
        request.getXMLBean().setDestination(IDP_URL);
        request.getXMLBean().setAssertionConsumerServiceURL(samlServletUri.toASCIIString());

        request.sign(credential.getKey(), credential.getCertificateChain());
        return request;
    }

    public static void main(String[] args) throws IOException, KeyStoreException {
        KeystoreCredential credential = new KeystoreCredential(
                "testing-spring-boot/src/main/resources/oxides.jks",
                "oxides".toCharArray(),
                "oxides".toCharArray(),
                "oxides",
                "JKS"
        );

        System.out.println(credential.getCertificate().getSubjectX500Principal().toString());
        System.out.println(credential.getSubjectName());
    }
}
