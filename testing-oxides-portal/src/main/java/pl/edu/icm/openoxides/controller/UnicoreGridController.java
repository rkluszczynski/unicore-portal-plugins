package pl.edu.icm.openoxides.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.icm.openoxides.saml.AuthenticationSession;
import pl.edu.icm.openoxides.saml.SamlRequestHandler;
import pl.edu.icm.openoxides.saml.SamlResponseHandler;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
//@RequestMapping(value = "/grid")
public class UnicoreGridController extends SamlAuthnController {
    private AuthenticationSession authenticationSession;
    private SessionData sessionData;

    @Autowired
    public UnicoreGridController(AuthenticationSession authenticationSession,
                                 SessionData sessionData,
                                 SamlRequestHandler samlRequestHandler,
                                 SamlResponseHandler samlResponseHandler
    ) {
        super(samlRequestHandler, samlResponseHandler);
        this.authenticationSession = authenticationSession;
        this.sessionData = sessionData;
    }

    @RequestMapping("/grid/storages")
    public void showGridUserStorageList(HttpServletResponse response,
                                        HttpSession session
    ) {
        log.info("GRID.1: " + session.getId());
        log.info("GRID`1: " + sessionData.getId());
//        log.info("      : " + session.getId());

        try {
//            response.sendRedirect("/redirected");
            response.sendRedirect(REQUEST_MAPPING_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Log log = LogFactory.getLog(UnicoreGridController.class);
}
