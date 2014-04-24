package pl.plgrid.unicore.portal.core.utils;

import eu.unicore.portal.core.Session;
import eu.unicore.util.httpclient.IClientConfiguration;

import javax.security.auth.login.CredentialException;

/**
 * Helper class for handling user security issues in portal.
 *
 * @author R.Kluszczynski
 */
public class SecurityHelper {

    public static IClientConfiguration getClientConfig() {
        return getSessionClientConfig(Session.getCurrent());
    }

    public static IClientConfiguration getSessionClientConfig(Session session) {
        IClientConfiguration clientConfiguration;
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
