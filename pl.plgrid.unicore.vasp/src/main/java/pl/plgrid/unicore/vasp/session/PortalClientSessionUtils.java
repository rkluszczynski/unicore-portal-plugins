package pl.plgrid.unicore.vasp.session;

import javax.security.auth.login.CredentialException;

import eu.unicore.portal.core.Session;
import eu.unicore.util.httpclient.IClientConfiguration;

/**
 *
 * @author rkluszczynski
 */
public class PortalClientSessionUtils {

    public static IClientConfiguration getCurrentSessionClientConfiguration() {
        return getSessionClientConfiguration(Session.getCurrent());
    }

    public static IClientConfiguration getSessionClientConfiguration(Session session) {
        IClientConfiguration clientConfiguration = null;
        try {
            clientConfiguration = session.getUser().getCredentials();
        } catch (CredentialException e) {
            throw new IllegalStateException(e);
        }
        return clientConfiguration;
    }
}
