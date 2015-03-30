package pl.edu.icm.openoxides.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.icm.openoxides.saml.AuthenticationSession;
import pl.edu.icm.openoxides.saml.SamlRequestHandler;
import pl.edu.icm.openoxides.saml.SamlResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
//@RequestMapping(SamlAuthnController.REQUEST_MAPPING_PATH)
public class SamlAuthnController {
    public static final String REQUEST_MAPPING_PATH = "/authn/saml";

    private final SamlRequestHandler samlRequestHandler;
    private final SamlResponseHandler samlResponseHandler;
    private AuthenticationSession authenticationSession;

    @Autowired
    public SamlAuthnController(SamlRequestHandler samlRequestHandler, SamlResponseHandler samlResponseHandler, AuthenticationSession authenticationSession) {
        this.samlRequestHandler = samlRequestHandler;
        this.samlResponseHandler = samlResponseHandler;
        this.authenticationSession = authenticationSession;
    }

    @RequestMapping(value = REQUEST_MAPPING_PATH, method = RequestMethod.GET)
    public void performAuthenticationRequest(HttpSession session, HttpServletResponse response) {
        log.info("SESSION_G: " + ((session == null) ? "NULL" : session.getId()));
        samlRequestHandler.performAuthenticationRequest(response, authenticationSession);
    }

    @RequestMapping(value = REQUEST_MAPPING_PATH, method = RequestMethod.POST)
    public void processAuthenticationResponse(HttpServletRequest request, HttpServletResponse response) {
        log.info("SESSION_P: " + request.getSession().getId());
        samlResponseHandler.processAuthenticationResponse(request, response, authenticationSession);
    }

    @RequestMapping("/grid/storages")
    public void showGridUserStorageList(HttpServletResponse response, HttpSession session) throws IOException {
        log.info("GRID.1: " + session.getId());
        if (authenticationSession.getTrustDelegations() == null) {
            authenticationSession.setIdpUrl(SamlRequestHandler.idpUrl);
            authenticationSession.setReturnUrl("/grid/storages");
            response.sendRedirect(SamlAuthnController.REQUEST_MAPPING_PATH);
            return;
        }
    }

    private Log log = LogFactory.getLog(SamlAuthnController.class);
}
