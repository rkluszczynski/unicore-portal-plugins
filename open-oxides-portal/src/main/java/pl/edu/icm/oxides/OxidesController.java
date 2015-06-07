package pl.edu.icm.oxides;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.icm.oxides.saml.AuthenticationSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
public class OxidesController {
    private AuthenticationSession authenticationSession;

    @Autowired
    public OxidesController(AuthenticationSession authenticationSession) {
        this.authenticationSession = authenticationSession;
    }

    @RequestMapping(value = "/redirect")
    @ResponseBody
    public String testRedirect(HttpServletResponse response, HttpSession session) throws IOException {
        log.info("TEST 1: " + session.getId());
        response.sendRedirect("/redirected");
        return "TEST1";
    }

    @RequestMapping(value = "/redirected", method = RequestMethod.GET)
    @ResponseBody
    public String testGetRedirected(HttpSession session) {
        log.info("TEST-G: " + session.getId());
        return "TEST-G";
    }

    @RequestMapping(value = "/redirected", method = RequestMethod.POST)
    @ResponseBody
    public String testPostRedirected(HttpServletRequest request) {
        log.info("TEST-P: " + request.getSession().getId());
        return "TEST-P";
    }

    private Log log = LogFactory.getLog(OxidesController.class);
}
