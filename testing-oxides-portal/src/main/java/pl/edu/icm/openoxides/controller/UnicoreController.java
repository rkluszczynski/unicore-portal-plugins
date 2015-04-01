package pl.edu.icm.openoxides.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.icm.openoxides.saml.AuthenticationSession;
import pl.edu.icm.openoxides.saml.SamlRequestHandler;
import pl.edu.icm.openoxides.unicore.TSSStorageHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pl.edu.icm.openoxides.saml.AuthenticationSession.AUTHENTICATION_SESSION_KEY;

//@RestController
public class UnicoreController {
    private final TSSStorageHandler tssStorageHandler;
    private AuthenticationSession authenticationSession;

    @Autowired
    public UnicoreController(TSSStorageHandler tssStorageHandler, AuthenticationSession authenticationSession) {
        this.tssStorageHandler = tssStorageHandler;
        this.authenticationSession = authenticationSession;
    }

    @RequestMapping("/unicore/storages")
//    @ResponseBody
    public void showUserStorageList(HttpServletRequest request, HttpServletResponse response) {
        log.info("SESSION.1: " + request.getSession().getId());
        log.info("         : " + authenticationSession.getUuid());
        try {
//            response.sendRedirect(SamlAuthnController.REQUEST_MAPPING_PATH);
            response.sendRedirect("/redirected");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return Collections.emptyList();

//        AuthenticationSession authenticationSession = (AuthenticationSession) request
//                .getSession()
//                .getAttribute(AUTHENTICATION_SESSION_KEY);
//        log.info("SESSION.2: " + request.getSession().getId());
//        if (shouldRedirectToAuthentication(authenticationSession)) {
//            log.info("SESSION.3: " + request.getSession().getId());
//            sendRedirection(request, response);
//            log.info("SESSION.4: " + request.getSession().getId());
//            return Collections.emptyList();
//        }
//        return tssStorageHandler.retrieveUserStorageList(authenticationSession);
    }

    private void sendRedirection(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationSession authenticationSession = new AuthenticationSession();
        authenticationSession.setIdpUrl(SamlRequestHandler.idpUrl);
//        authenticationSession.setReturnUrl(request.getServletPath());
//        authenticationSession.setReturnUrl(request.getContextPath());
        authenticationSession.setReturnUrl("/unicore/storages");

        request.getSession().setAttribute(AUTHENTICATION_SESSION_KEY, authenticationSession);

        try {
            response.sendRedirect(SamlAuthnController.REQUEST_MAPPING_PATH);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private boolean shouldRedirectToAuthentication(AuthenticationSession session) {
        return session == null;
    }

    private Log log = LogFactory.getLog(UnicoreController.class);
}
