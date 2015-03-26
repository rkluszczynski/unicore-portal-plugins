package pl.edu.icm.openoxides.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.icm.openoxides.saml.SamlRequestHandler;
import pl.edu.icm.openoxides.saml.SamlResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(SamlAuthnController.REQUEST_MAPPING_PATH)
public class SamlAuthnController {
    public static final String REQUEST_MAPPING_PATH = "/authn/saml";

    private final SamlRequestHandler samlRequestHandler;
    private final SamlResponseHandler samlResponseHandler;

    @Autowired
    public SamlAuthnController(SamlRequestHandler samlRequestHandler, SamlResponseHandler samlResponseHandler) {
        this.samlRequestHandler = samlRequestHandler;
        this.samlResponseHandler = samlResponseHandler;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void performAuthenticationRequest(HttpServletResponse response) {
        samlRequestHandler.performAuthenticationRequest(response);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String processAuthenticationResponse(HttpServletRequest request) {
        return samlResponseHandler.processAuthenticationResponse(request);
    }
}
