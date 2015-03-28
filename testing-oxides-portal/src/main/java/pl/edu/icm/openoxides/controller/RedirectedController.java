package pl.edu.icm.openoxides.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.icm.openoxides.saml.AuthenticationSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/redirected")
public class RedirectedController {
    private AuthenticationSession authenticationSession;
    private SessionData sessionData;

    @Autowired
    public RedirectedController(AuthenticationSession authenticationSession, SessionData sessionData) {
        this.authenticationSession = authenticationSession;
        this.sessionData = sessionData;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String testGetRedirected(HttpSession session) {
        log.info("TEST-G: " + session.getId());
        log.info("TEST`G: " + sessionData.getId());
        return "TEST-G";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String testPostRedirected(HttpServletRequest request) {
        log.info("TEST-P: " + request.getSession().getId());
        log.info("TEST-P: " + authenticationSession.getUuid());
        return "TEST-P";
    }

    private Log log = LogFactory.getLog(RedirectedController.class);
}
