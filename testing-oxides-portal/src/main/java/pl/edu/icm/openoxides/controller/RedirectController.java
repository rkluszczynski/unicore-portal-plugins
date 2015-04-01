package pl.edu.icm.openoxides.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.icm.openoxides.saml.AuthenticationSession;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//@RestController
public class RedirectController {
    private AuthenticationSession authenticationSession;
    private SessionData sessionData;

    @Autowired
    public RedirectController(AuthenticationSession authenticationSession, SessionData sessionData) {
        this.authenticationSession = authenticationSession;
        this.sessionData = sessionData;
    }

    @RequestMapping("/redirect")
    @ResponseBody
    public String testRedirect(HttpServletResponse response,
                               HttpSession session
    ) throws IOException {
        log.info("TEST 1: " + session.getId());
        log.info("TEST`1: " + sessionData.getId());
        response.sendRedirect("/redirected");
        return "TEST1";
    }

    private Log log = LogFactory.getLog(RedirectController.class);
}
