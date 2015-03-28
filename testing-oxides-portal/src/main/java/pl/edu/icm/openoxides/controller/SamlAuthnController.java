package pl.edu.icm.openoxides.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.edu.icm.openoxides.saml.SamlRequestHandler;
import pl.edu.icm.openoxides.saml.SamlResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@RestController
//@RequestMapping(SamlAuthnController.REQUEST_MAPPING_PATH)
public class SamlAuthnController {
    public static final String REQUEST_MAPPING_PATH = "/authn/saml";

    private final SamlRequestHandler samlRequestHandler;
    private final SamlResponseHandler samlResponseHandler;

    @Autowired
    public SamlAuthnController(SamlRequestHandler samlRequestHandler, SamlResponseHandler samlResponseHandler) {
        this.samlRequestHandler = samlRequestHandler;
        this.samlResponseHandler = samlResponseHandler;
    }

    @RequestMapping(value = REQUEST_MAPPING_PATH, method = RequestMethod.GET)
    public void performAuthenticationRequest(HttpServletRequest request, HttpServletResponse response) {
        log.info("SESSION_G: " + request.getSession().getId());
        samlRequestHandler.performAuthenticationRequest(request, response);
    }

    @RequestMapping(value = REQUEST_MAPPING_PATH, method = RequestMethod.POST)
//    @ResponseBody
    public void processAuthenticationResponse(HttpServletRequest request, HttpServletResponse response) {
        log.info("SESSION: " + request.getSession().getId());
        samlResponseHandler.processAuthenticationResponse(request, response);
    }

    private Log log = LogFactory.getLog(SamlAuthnController.class);
}
