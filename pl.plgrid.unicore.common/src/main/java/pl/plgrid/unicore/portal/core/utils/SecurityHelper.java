package pl.plgrid.unicore.portal.core.utils;

import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.authn.UserMetadataAttribute;
import eu.unicore.util.httpclient.IClientConfiguration;
import org.apache.log4j.Logger;

import javax.security.auth.login.CredentialException;

/**
 * Helper class for handling user security issues in portal.
 *
 * @author R.Kluszczynski
 */
final
public class SecurityHelper {
    public static UserMetadataAttribute getUserAttributes() {
        return Session.getCurrent()
                .getUser()
                .getAttribute(UserMetadataAttribute.class);
    }

    public static IClientConfiguration getClientConfig() {
        return getSessionClientConfig(Session.getCurrent());
    }

    private static IClientConfiguration getSessionClientConfig(Session session) {
        IClientConfiguration clientConfiguration;
        try {
            clientConfiguration = session
                    .getUser()
                    .getCredentials();
        } catch (CredentialException e) {
            logger.warn("CredentialException: ", e);
            throw new IllegalStateException(e);
        }
        return clientConfiguration;
    }

    private SecurityHelper() {
    }

    private static final Logger logger = Logger.getLogger(SecurityHelper.class);
}
