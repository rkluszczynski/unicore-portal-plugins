package pl.plgrid.unicore.portal.core.utils;

import eu.unicore.portal.core.Session;
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
    private static final Logger logger = Logger.getLogger(SecurityHelper.class);

    public static IClientConfiguration getClientConfig() {
        return getSessionClientConfig(Session.getCurrent());
    }

    public static IClientConfiguration getSessionClientConfig(Session session) {
        IClientConfiguration clientConfiguration;
        try {
            clientConfiguration = session
                    .getUser()
                    .getCredentials();
//            logger.info("getExtraSecurityTokens(): " + clientConfiguration.getExtraSecurityTokens());
//
//            String userAccountAttributes = session
//                    .getUser()
//                    .toAccount()
//                    .getAttributes()
//                    .toString();
//            logger.info("userAccountAttributes: " + userAccountAttributes);
//
//            String userAccountExtendedAttributes = session
//                    .getUser()
//                    .toAccount()
//                    .getExtendedAttributes();
//            logger.info("userAccountExtendedAttributes: " + userAccountExtendedAttributes);

        } catch (CredentialException e) {
            logger.warn("CredentialException: ", e);
            throw new IllegalStateException(e);
        }
        return clientConfiguration;
    }

    private SecurityHelper() {
    }
}
