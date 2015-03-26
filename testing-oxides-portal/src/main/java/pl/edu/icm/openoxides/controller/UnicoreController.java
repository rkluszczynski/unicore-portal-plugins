package pl.edu.icm.openoxides.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.icm.openoxides.saml.AuthenticationSession;
import pl.edu.icm.openoxides.saml.SamlRequestHandler;
import pl.edu.icm.openoxides.service.input.UnicoreStorage;
import pl.edu.icm.openoxides.unicore.TSSStorageHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static pl.edu.icm.openoxides.saml.AuthenticationSession.AUTHENTICATION_SESSION_KEY;

@RestController
@RequestMapping("/unicore")
public class UnicoreController {
    private final TSSStorageHandler tssStorageHandler;

    @Autowired
    public UnicoreController(TSSStorageHandler tssStorageHandler) {
        this.tssStorageHandler = tssStorageHandler;
    }

    @RequestMapping("/storages")
    @ResponseBody
    public List<UnicoreStorage> showUserStorageList(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationSession authenticationSession = (AuthenticationSession) request
                .getSession()
                .getAttribute(AUTHENTICATION_SESSION_KEY);
        if (shouldRedirectToAuthentication(authenticationSession)) {
            sendRedirection(request, response);
            return null;
        }
        return tssStorageHandler.retrieveUserStorageList(authenticationSession);
    }

    private void sendRedirection(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationSession authenticationSession = new AuthenticationSession();
        authenticationSession.setIdpUrl(SamlRequestHandler.idpUrl);
//        authenticationSession.setReturnUrl(request.getServletPath());
        authenticationSession.setReturnUrl(request.getContextPath());

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

    private Log log = LogFactory.getLog(SamlRequestHandler.class);
}
