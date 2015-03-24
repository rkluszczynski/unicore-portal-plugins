package pl.edu.icm.openoxides.tmp;

import eu.unicore.samly2.SAMLUtils;
import eu.unicore.samly2.exceptions.SAMLValidationException;
import org.apache.xmlbeans.XmlException;
import org.springframework.web.bind.annotation.RestController;
import xmlbeans.org.oasis.saml2.assertion.AssertionDocument;
import xmlbeans.org.oasis.saml2.assertion.AssertionType;
import xmlbeans.org.oasis.saml2.assertion.NameIDType;
import xmlbeans.org.oasis.saml2.protocol.ResponseDocument;
import xmlbeans.org.oasis.saml2.protocol.ResponseType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SamlAuthnTestController {
    public SamlAuthnTestController() throws Exception {
//        idpUrl = "https://unity.grid.icm.edu.pl/testbed-portal/saml2unicoreIdp-web";
//        targetUrl = "https://unicore.studmat.umk.pl:9091/authn";
//        targetUrl = "https://localhost:8443/done";

//        AuthnRequest request = createRequest();
//        authenticationContext = new SamlAuthenticationContext(idpUrl,
//                request.getXMLBeanDoc().xmlText(),
//                "http://wp.pl",
//                request.getXMLBean().getID(),
//                getUniqueIdentifier()
//        );
    }

    public void processResponse(ResponseDocument response, StringBuffer buffer) throws Exception {
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

}
