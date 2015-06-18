package pl.edu.icm.oxides;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import pl.edu.icm.oxides.saml.AuthenticationSession;
import pl.edu.icm.oxides.saml.OxidesSamlRequestHandler;
import pl.edu.icm.oxides.saml.OxidesSamlResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@SessionAttributes("authenticationSession")
@RequestMapping(value = "/oxides")
public class OxidesController {
    private final OxidesSamlRequestHandler samlRequestHandler;
    private final OxidesSamlResponseHandler samlResponseHandler;
    private AuthenticationSession authenticationSession;

    @Autowired
    public OxidesController(OxidesSamlRequestHandler samlRequestHandler, OxidesSamlResponseHandler samlResponseHandler,
                            AuthenticationSession authenticationSession) {
        this.samlRequestHandler = samlRequestHandler;
        this.samlResponseHandler = samlResponseHandler;
        this.authenticationSession = authenticationSession;
    }

    @RequestMapping(value = "/")
    public void mainView(HttpSession session, HttpServletResponse response) throws IOException {
        authenticationSession.setReturnUrl("/oxides/final");

        log.info("TEST-0: " + session.getId());
        log.info("TEST-0: " + authenticationSession);
        response.sendRedirect("/oxides/authn");
    }

    @RequestMapping(value = "/authn", method = RequestMethod.GET)
    public void performAuthenticationRequest(HttpSession session, HttpServletResponse response) {
        log.info("SAML-G: " + session.getId());
        log.info("SAML-G: " + authenticationSession);
        samlRequestHandler.performAuthenticationRequest(response, authenticationSession);
    }

    @RequestMapping(value = "/authn", method = RequestMethod.POST)
    public void processAuthenticationResponse(HttpServletRequest request, HttpServletResponse response) {
        log.info("SAML-P: " + request.getSession().getId());
        log.info("SAML-P: " + authenticationSession);
        samlResponseHandler.processAuthenticationResponse(request, response, authenticationSession);
    }

    @RequestMapping(value = "/final")
    @ResponseBody
    public String finalPage(HttpSession session, HttpServletResponse response) throws IOException {
        log.info("TEST-F: " + session.getId());
        log.info("TEST-F: " + authenticationSession);
        return authenticationSession.toString() + "<p><a href=\"/oxides/linked\">link</a></p>";
    }

    @RequestMapping(value = "/linked")
    @ResponseBody
    public String linkedPage(HttpSession session, HttpServletResponse response) throws IOException {
        log.info("LINKED: " + session.getId());
        log.info("LINKED: " + authenticationSession);
        return authenticationSession.toString();
    }

    private Log log = LogFactory.getLog(OxidesController.class);
}
