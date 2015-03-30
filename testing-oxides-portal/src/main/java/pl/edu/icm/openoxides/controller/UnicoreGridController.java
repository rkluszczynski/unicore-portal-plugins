package pl.edu.icm.openoxides.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.icm.openoxides.saml.AuthenticationSession;
import pl.edu.icm.openoxides.saml.SamlRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//@RestController
public class UnicoreGridController {
    private AuthenticationSession authenticationSession;

    @Autowired
    public UnicoreGridController(AuthenticationSession authenticationSession) {
        this.authenticationSession = authenticationSession;
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

    private void sendRedirection(HttpServletRequest request) {
        authenticationSession.setIdpUrl(SamlRequestHandler.idpUrl);
//        authenticationSession.setReturnUrl(request.getServletPath());
//        authenticationSession.setReturnUrl(request.getContextPath());
        authenticationSession.setReturnUrl("/grid/storages");
    }

    private Log log = LogFactory.getLog(UnicoreGridController.class);
}
