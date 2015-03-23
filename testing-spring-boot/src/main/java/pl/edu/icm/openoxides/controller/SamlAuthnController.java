package pl.edu.icm.openoxides.controller;

import de.fzj.unicore.wsrflite.xmlbeans.client.RegistryClient;
import eu.emi.security.authn.x509.impl.KeystoreCredential;
import eu.unicore.samly2.SAMLConstants;
import eu.unicore.samly2.SAMLUtils;
import eu.unicore.samly2.binding.HttpPostBindingSupport;
import eu.unicore.samly2.binding.SAMLMessageType;
import eu.unicore.samly2.elements.NameID;
import eu.unicore.samly2.exceptions.SAMLValidationException;
import eu.unicore.samly2.proto.AuthnRequest;
import eu.unicore.security.dsig.DSigException;
import eu.unicore.util.httpclient.ClientProperties;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.util.Base64;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import xmlbeans.org.oasis.saml2.assertion.AssertionDocument;
import xmlbeans.org.oasis.saml2.assertion.AssertionType;
import xmlbeans.org.oasis.saml2.assertion.NameIDType;
import xmlbeans.org.oasis.saml2.protocol.AuthnRequestDocument;
import xmlbeans.org.oasis.saml2.protocol.ResponseDocument;
import xmlbeans.org.oasis.saml2.protocol.ResponseType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SamlAuthnController {
    private SamlAuthenticationContext authenticationContext;
    public final String idpUrl;
    public final String targetUrl;

    public SamlAuthnController() throws Exception {
        idpUrl = "https://unity.grid.icm.edu.pl/testbed-portal/saml2unicoreIdp-web";
//        targetUrl = "https://unicore.studmat.umk.pl:9091/authn";
        targetUrl = "https://localhost:8443/done";

        AuthnRequest request = createRequest();
        authenticationContext = new SamlAuthenticationContext(idpUrl,
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

    @RequestMapping(value = "/done", method = RequestMethod.POST)
    public String doneAuthnWithUnity(HttpServletRequest request, HttpServletResponse response,
                                     @RequestBody String body) throws Exception {
        String samlResponse = request.getParameter("SAMLResponse");

        StringBuffer buffer = new StringBuffer();
        buffer.append("POST was it! <br />");
        buffer.append(String.format("<b>BODY</b>: <br />%s<br />", body));
        processResponse(samlResponse, buffer);
        return buffer.toString();
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
        URI samlServletUri = new URI(targetUrl);

        KeystoreCredential credential = new KeystoreCredential(
                "testing-spring-boot/src/main/resources/oxides.jks",
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

    private void processResponse(String responseRaw, StringBuffer buffer) throws Exception {
        ResponseDocument response = decodeResponse(responseRaw);

//        SamlTrustChecker trustChecker = new TruststoreBasedSamlTrustChecker(idpValidator, false);
//
//        SSOAuthnResponseValidator validator = new SSOAuthnResponseValidator(
//                myName,
//                new URI(targetUrl).toASCIIString(),
//                input.getRequestId(),
//                AssertionValidator.DEFAULT_VALIDITY_GRACE_PERIOD,
//                trustChecker,
//                replayChecker,
//                SAMLBindings.HTTP_POST);
//        validator.validate(response);

        String issuerUri = response.getResponse().getIssuer().getStringValue();
        buffer.append("<b>ISSUER URI</b>: " + issuerUri + "<br/>");

        List<AssertionDocument> assertionDocuments = processAssertions(response);
        List<NameIDType> authenticatedUsers = assertionDocuments
                .stream()
                .map(document -> document.getAssertion().getSubject().getNameID())
                .collect(Collectors.toList());
        buffer.append(String.format("<b>AUTHENTICATED USER [%d]</b>: <br/><ul>", authenticatedUsers.size()));
        for (NameIDType user : authenticatedUsers) {
            buffer.append(String.format("<li>%s</li>", user.toString()));
        }
        buffer.append("</ul><br/>");
//
//        onSamlResponse(issuerUri, authenticatedUser, response, validator.getOtherAssertions());
        for (AssertionDocument assertionDocument : assertionDocuments) {
            OxidesHandler.onSamlResponse(assertionDocument);
        }
    }

    private List<AssertionDocument> processAssertions(ResponseDocument responseDocument) throws IOException, XmlException, SAMLValidationException {
        List<AssertionDocument> assertionDocumentList = new ArrayList();

        ResponseType response = responseDocument.getResponse();
        NameIDType issuer = response.getIssuer();
        if (issuer != null && issuer.getFormat() != null && !issuer.getFormat().equals("urn:oasis:names:tc:SAML:2.0:nameid-format:entity")) {
            throw new SAMLValidationException("Issuer of SAML response must be of Entity type in SSO AuthN. It is: " + issuer.getFormat());
        } else {
//            SSOAuthnAssertionValidator authnAsValidator = new SSOAuthnAssertionValidator(this.consumerSamlName, this.consumerEndpointUri, this.requestId, this.samlValidityGraceTime, this.trustChecker, this.replayChecker, this.binding);
//            AssertionValidator asValidator = new AssertionValidator(this.consumerSamlName, this.consumerEndpointUri, (String)null, this.samlValidityGraceTime, this.trustChecker);

            AssertionDocument[] assertions = SAMLUtils.getAssertions(response);
            for (AssertionDocument assertionDocument : assertions) {
//                System.out.println(assertionDocument.xmlText());
                AssertionType assertion = assertionDocument.getAssertion();

                if (assertion.sizeOfAuthnStatementArray() > 0) {
//                    this.tryValidateAsAuthnAssertion(authnAsValidator, assertionDoc);
                    assertionDocumentList.add(assertionDocument);
                }

                if (assertion.sizeOfStatementArray() > 0 || assertion.sizeOfAttributeStatementArray() > 0 || assertion.sizeOfAuthzDecisionStatementArray() > 0) {
//                    this.tryValidateAsGenericAssertion(asValidator, assertionDoc);
                    assertionDocumentList.add(assertionDocument);
                }

                if (issuer == null) {
                    issuer = assertion.getIssuer();
                } else if (!issuer.getStringValue().equals(assertion.getIssuer().getStringValue())) {
                    throw new SAMLValidationException("Inconsistent issuer in assertion: " + assertion.getIssuer() + ", previously had: " + issuer);
                }
            }

//            if(this.authNAssertions.size() == 0) {
//                if(this.reasons.getSize() > 0) {
//                    throw new SAMLValidationException("Authentication assertion(s) was found, but it was not correct wrt SSO profile: " + this.reasons);
//                } else {
//                    throw new SAMLValidationException("There was no authentication assertion found in the SAML response");
//                }
//            }
        }
        return assertionDocumentList;
    }

//    protected void tryValidateAsGenericAssertion(AssertionValidator asValidator, AssertionDocument assertionDoc) throws SAMLValidationException {
//        asValidator.validate(assertionDoc);
//        AssertionType assertion = assertionDoc.getAssertion();
//        NameIDType asIssuer = assertion.getIssuer();
//        if(asIssuer.getFormat() != null && !asIssuer.getFormat().equals("urn:oasis:names:tc:SAML:2.0:nameid-format:entity")) {
//            throw new SAMLValidationException("Issuer of assertion must be of Entity type in SSO AuthN. It is: " + asIssuer.getFormat());
//        } else if(this.binding == SAMLBindings.HTTP_POST && (assertion.getSignature() == null || assertion.getSignature().isNil())) {
//            throw new SAMLValidationException("Assertion is not signed in the SSO authN used over HTTP POST, while should be.");
//        } else {
//            this.otherAssertions.add(assertionDoc);
//            if(assertion.sizeOfAttributeStatementArray() > 0) {
//                this.attributeAssertions.add(assertionDoc);
//            }
//
//        }
//    }

    private ResponseDocument decodeResponse(String response) throws SAMLValidationException {
        byte[] decoded = Base64.decode(response.getBytes());
        if (decoded == null)
            throw new SAMLValidationException("The SAML response is not properly Base 64 encoded");
        String respString = new String(decoded, Charset.forName("UTF-8"));
        try {
            return ResponseDocument.Factory.parse(respString);
        } catch (XmlException e) {
            throw new SAMLValidationException(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        KeystoreCredential credential = new KeystoreCredential(
                "testing-spring-boot/src/main/resources/oxides.jks",
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

        RegistryClient registryClient = new RegistryClient(registryEpr,
                new ClientProperties("testing-spring-boot/src/main/resources/application.properties")
        );
        QName qName = new QName("http://unigrids.org/2006/04/services/tsf", "TargetSystemFactory");
        for (EndpointReferenceType epr : registryClient.listAccessibleServices(qName)) {
            System.out.println(" -> " + epr.getAddress().getStringValue());
        }
        System.out.println("DONE");
    }
}
