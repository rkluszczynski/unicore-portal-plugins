package pl.edu.icm.oxides.unicore;

import org.springframework.stereotype.Component;
import pl.edu.icm.oxides.saml.AuthenticationSession;

@Component
public class UnicoreGridHandler {
    public String listUserSites(AuthenticationSession authenticationSession) {
        return "TODO: sites";
    }

    public String listUserStorages(AuthenticationSession authenticationSession) {
        return "TODO: storages";
    }

    public String listUserJobs(AuthenticationSession authenticationSession) {
        return "TODO: jobs";
    }
}
