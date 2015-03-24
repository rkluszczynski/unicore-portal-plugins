package pl.edu.icm.openoxides.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.icm.openoxides.service.SamlRequestHandler;
import pl.edu.icm.openoxides.service.SamlResponseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/authn/saml")
public class SamlAuthnController {
    private final SamlRequestHandler samlRequestHandler;
    private final SamlResponseHandler samlResponseHandler;

    @Autowired
    public SamlAuthnController(SamlRequestHandler samlRequestHandler, SamlResponseHandler samlResponseHandler) {
        this.samlRequestHandler = samlRequestHandler;
        this.samlResponseHandler = samlResponseHandler;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void performAuthentication(HttpServletResponse response) {
        samlRequestHandler.performAuthenticationRequest(response);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String processAuthenticationResponse(HttpServletRequest request, @RequestBody String body) {
        return samlResponseHandler.processAuthenticationResponse(request, body);
    }
}
