package pl.edu.icm.openoxides.service;

import eu.unicore.samly2.exceptions.SAMLValidationException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xmlbeans.org.oasis.saml2.protocol.ResponseDocument;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Component
public class SamlResponseHandler {
    private final OxidesDataPortalService dataPortalService;

    @Autowired
    public SamlResponseHandler(OxidesDataPortalService oxidesDataPortalService) {
        this.dataPortalService = oxidesDataPortalService;
    }

    public String processAuthenticationResponse(HttpServletRequest request) {
        String samlResponse = request.getParameter("SAMLResponse");

        StringBuffer buffer = new StringBuffer();
        try {
            ResponseDocument response = decodeResponse(samlResponse);
            dataPortalService.processResponse(response, buffer);
        } catch (Exception e) {
            buffer.append(String.format("<b>ERROR</b>: <br />%s<br />", e.getMessage()));
            e.printStackTrace(System.err);
        }
        return buffer.toString();
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
}
