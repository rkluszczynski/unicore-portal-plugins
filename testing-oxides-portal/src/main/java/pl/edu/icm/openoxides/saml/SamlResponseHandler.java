package pl.edu.icm.openoxides.saml;

import eu.unicore.samly2.exceptions.SAMLValidationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.icm.openoxides.service.OxidesDataPortalService;
import pl.edu.icm.openoxides.service.input.OxidesPortalData;
import xmlbeans.org.oasis.saml2.protocol.ResponseDocument;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

import static pl.edu.icm.openoxides.saml.AuthenticationSession.AUTHENTICATION_SESSION_KEY;
import static pl.edu.icm.openoxides.service.OxidesDataUploadService.OXIDES_JSON_SESSION_ATTRIBUTE_KEY;
import static pl.edu.icm.openoxides.service.input.OxidesPortalData.OXIDES_DATA_SESSION_ATTRIBUTE_KEY;

@Component
public class SamlResponseHandler {
    private final OxidesDataPortalService dataPortalService;

    @Autowired
    public SamlResponseHandler(OxidesDataPortalService oxidesDataPortalService) {
        this.dataPortalService = oxidesDataPortalService;
    }

    public String processAuthenticationResponse(HttpServletRequest request, HttpServletResponse response1) {
        String samlResponse = request.getParameter("SAMLResponse");
        HttpSession session = request.getSession();
        OxidesPortalData oxidesData = (OxidesPortalData) session.getAttribute(OXIDES_DATA_SESSION_ATTRIBUTE_KEY);
        if (oxidesData == null) {
            oxidesData = new OxidesPortalData("OpenOxidesGrid");
        }

        StringBuffer buffer = new StringBuffer();
        try {
            AuthenticationSession authenticationSession = (AuthenticationSession) request
                    .getSession()
                    .getAttribute(AUTHENTICATION_SESSION_KEY);
            log.warn(authenticationSession);

            ResponseDocument response = decodeResponse(samlResponse);
            String oxidesJsonUri = dataPortalService.processResponse(response, oxidesData, authenticationSession, buffer);

            session.setAttribute(OXIDES_JSON_SESSION_ATTRIBUTE_KEY, oxidesJsonUri);

            buffer.append(oxidesJsonUri);


            if (authenticationSession != null) {
                response1.sendRedirect(authenticationSession.getReturnUrl());
                return "";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return buffer.toString();
    }

    private void sendRedirection(HttpServletRequest request, HttpServletResponse response1) {

    }

    private ResponseDocument decodeResponse(String response) throws SAMLValidationException {
        byte[] decoded = Base64.decode(response.getBytes());
        if (decoded == null)
            throw new SAMLValidationException("The SAML response is not properly Base 64 encoded");
        String respString = new String(decoded, StandardCharsets.UTF_8);
        try {
            return ResponseDocument.Factory.parse(respString);
        } catch (XmlException e) {
            throw new SAMLValidationException(e.getMessage());
        }
    }

    private Log log = LogFactory.getLog(SamlResponseHandler.class);
}
