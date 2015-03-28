package pl.edu.icm.openoxides.service;

import eu.unicore.samly2.SAMLBindings;
import eu.unicore.samly2.exceptions.SAMLValidationException;
import eu.unicore.samly2.trust.SamlTrustChecker;
import eu.unicore.samly2.trust.TruststoreBasedSamlTrustChecker;
import eu.unicore.samly2.validators.AssertionValidator;
import eu.unicore.samly2.validators.ReplayAttackChecker;
import eu.unicore.samly2.validators.SSOAuthnResponseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.openoxides.config.GridIdentityProvider;
import pl.edu.icm.openoxides.saml.AuthenticationSession;
import pl.edu.icm.openoxides.saml.ResponseDocumentWrapper;
import pl.edu.icm.openoxides.saml.SamlRequestHandler;
import pl.edu.icm.openoxides.service.input.OxidesPortalData;
import xmlbeans.org.oasis.saml2.protocol.ResponseDocument;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class OxidesDataPortalService {
    private final GridIdentityProvider identityProvider;
    private final OxidesDataUploadService dataUploadService;

    @Autowired
    public OxidesDataPortalService(GridIdentityProvider identityProvider, OxidesDataUploadService dataUploadService) {
        this.identityProvider = identityProvider;
        this.dataUploadService = dataUploadService;
    }

    public String processResponse(ResponseDocument response, OxidesPortalData oxidesData, AuthenticationSession authenticationSession, StringBuffer buffer) throws Exception {
//        SSOAuthnResponseValidator validator =
        validateSamlResponse(response);
//        printAssertions(buffer, "AUTHN", validator.getAuthNAssertions());
//        printAssertions(buffer, "ATTR", validator.getAttributeAssertions());
//        printAssertions(buffer, "OTHER", validator.getOtherAssertions());

        ResponseDocumentWrapper documentWrapper = new ResponseDocumentWrapper(response);
        String oxidesJsonUri = dataUploadService.onSamlResponse(documentWrapper, oxidesData, authenticationSession);

//        String issuerUri = response.getResponse().getIssuer().getStringValue();
//        buffer.append("<b>ISSUER URI</b>: " + issuerUri + "<br/>");
        return oxidesJsonUri;
    }

//    private void printAssertions(StringBuffer buffer, String caption, List<AssertionDocument> assertions) {
//        buffer.append(String.format("<b>ASSERTIONS %s [%d]</b>: <br/><ul>", caption, assertions.size()));
//        for (AssertionDocument assertion : assertions) {
//            NameIDType nameID = assertion.getAssertion().getSubject().getNameID();
//            buffer.append(String.format("<li>%s</li>", nameID.toString()));
//        }
//        buffer.append("</ul><br/>");
//    }

    private SSOAuthnResponseValidator validateSamlResponse(ResponseDocument response) throws URISyntaxException, SAMLValidationException {
        SamlTrustChecker trustChecker = new TruststoreBasedSamlTrustChecker(
                identityProvider.getIdpValidator(),
                false
        );
        SSOAuthnResponseValidator validator = new SSOAuthnResponseValidator(
                identityProvider.getGridCredential().getSubjectName(),
                new URI(SamlRequestHandler.targetUrl).toASCIIString(),
                SamlRequestHandler.requestId,
                AssertionValidator.DEFAULT_VALIDITY_GRACE_PERIOD,
                trustChecker,
                new ReplayAttackChecker(),
                SAMLBindings.HTTP_POST
        );
        validator.validate(response);
        return validator;
    }
}
