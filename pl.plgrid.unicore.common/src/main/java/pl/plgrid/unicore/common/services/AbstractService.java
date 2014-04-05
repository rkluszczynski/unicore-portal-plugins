package pl.plgrid.unicore.common.services;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import de.fzj.unicore.wsrflite.xfire.WSRFClientFactory;
import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import eu.unicore.portal.core.Session;
import eu.unicore.security.wsutil.client.UnicoreWSClientFactory;
import eu.unicore.util.httpclient.IClientConfiguration;
import org.apache.log4j.Logger;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.common.utils.SecurityHelper;

import javax.security.auth.x500.X500Principal;
import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.util.List;

abstract class AbstractService {
    protected static final Logger logger = Logger.getLogger(AbstractService.class);

    private static final InternalRegistryService internalRegistryService = new InternalRegistryService();
    private static final WSRFClientFactory wsrfClientFactory = new WSRFClientFactory(
            SecurityHelper.getClientConfig()
    );


    abstract public <T> T createClient() throws UnavailableGridServiceException;

    protected <T> T getServiceClient(Class<T> clazz, QName port) throws UnavailableGridServiceException {
        List<EndpointReferenceType> accessibleServices = getAccessibleServices(port);

        // FIXME: do it better in round-robin way
        for (EndpointReferenceType accessibleServiceEpr : accessibleServices) {
            try {
                return initializeClient(clazz, accessibleServiceEpr);
            } catch (MalformedURLException ex) {
                logger.error("Problem during client initialization of class " + clazz.getCanonicalName(), ex);
            }
        }
        throw new UnavailableGridServiceException("No services of class " + clazz.getCanonicalName());
    }

    protected <T> List<EndpointReferenceType> getAccessibleServices(QName port) throws UnavailableGridServiceException {
        List<EndpointReferenceType> accessibleServicesList = Lists
                .newArrayList();
        try {
            accessibleServicesList.addAll(internalRegistryService
                    .getRegistryClient()
                    .listAccessibleServices(port));
        } catch (Exception e) {
            String message = "Problem with listing accessible services of port <" + port.toString() + ">";
            logger.warn(message, e);
            throw new UnavailableGridServiceException(message, e);
        }
        if (accessibleServicesList.isEmpty()) {
            String message = "No service of class <" + port.toString() + "> in registry!";
            logger.warn(message);
            throw new UnavailableGridServiceException(message);
        }
        return accessibleServicesList;
    }


    protected <T> T initializeClient(Class<T> clazz, EndpointReferenceType serviceEpr) throws MalformedURLException {
        logger.info("Using service: <" + serviceEpr.getAddress().getStringValue()
                + "> by user: <" + Session.getCurrent().getUser().getUsername()
                + ">");
        String receiverDn = WSUtilities.extractServerIDFromEPR(serviceEpr);
        IClientConfiguration clientConfig = SecurityHelper.getClientConfig();
        if (receiverDn != null) {
            clientConfig.getETDSettings().setReceiver(
                    new X500Principal(receiverDn));
        }
        return new UnicoreWSClientFactory(clientConfig)
                .createPlainWSProxy(clazz, serviceEpr
                        .getAddress()
                        .getStringValue());
        // TODO: think of retry feature (included in WSRFClientFactory)
//        return wsrfClientFactory.createPlainWSProxy(clazz, serviceEpr
//                .getAddress()
//                .getStringValue());
    }
}
