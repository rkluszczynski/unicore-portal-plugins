package pl.plgrid.unicore.common.utils;

import eu.unicore.portal.core.Session;
import eu.unicore.util.httpclient.IClientConfiguration;

import javax.security.auth.login.CredentialException;

/**
 * @author rkluszczynski
 */
public class SecurityHelper {

    public static IClientConfiguration getClientConfig() {
        return getSessionClientConfig(Session.getCurrent());
    }

    public static IClientConfiguration getSessionClientConfig(Session session) {
        IClientConfiguration clientConfiguration = null;
        try {
            clientConfiguration = session
                    .getUser()
                    .getCredentials();
        } catch (CredentialException e) {
            throw new IllegalStateException(e);
        }
        return clientConfiguration;
    }
}
