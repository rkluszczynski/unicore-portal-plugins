package pl.plgrid.unicore.vasp.session;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import de.fzj.unicore.uas.StorageFactory;
import de.fzj.unicore.uas.TargetSystemFactory;
import de.fzj.unicore.uas.client.StorageClient;
import de.fzj.unicore.uas.client.StorageFactoryClient;
import de.fzj.unicore.uas.client.TSFClient;
import de.fzj.unicore.uas.client.TSSClient;
import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import de.fzj.unicore.wsrflite.xmlbeans.client.RegistryClient;
import eu.unicore.portal.core.GlobalState;
import eu.unicore.portal.core.PortalConfiguration;
import eu.unicore.portal.core.Session;
import eu.unicore.security.wsutil.client.UnicoreWSClientFactory;
import eu.unicore.util.httpclient.IClientConfiguration;
import org.apache.log4j.Logger;
import org.chemomentum.common.ws.IServiceOrchestrator;
import org.unigrids.x2006.x04.services.tsf.CreateTSRDocument;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.utils.SecurityHelper;

import javax.security.auth.x500.X500Principal;
import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;

/**
 * @author rkluszczynski
 */
public class ServiceClientsFactory {

    private static final Logger logger = Logger
            .getLogger(ServiceClientsFactory.class);

    private IClientConfiguration clientConfiguration;
    private RegistryClient registryClient;

    public ServiceClientsFactory() {
        this(GlobalState
                .getCurrent()
                .getPortalConfiguration()
                .getProperties()
                .getProperty(
                        PortalConfiguration.CORE_PREFIX
                                + PortalConfiguration.REGISTRIES
                ), null);
    }

    public ServiceClientsFactory(String registryURL,
                                 IClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration == null ? SecurityHelper
                .getClientConfig() : clientConfiguration;
        this.registryClient = createRegistryClient(registryURL);
    }

    public List<TSSClient> getTSSClients() throws Exception {
        // TODO: handle exceptions in a way that valid TSSes are returned
        // now, problem with one raises exception for all
        List<EndpointReferenceType> tsfs = getRegistryClient()
                .listAccessibleServices(TargetSystemFactory.TSF_PORT);
        List<TSSClient> tssClientsList = Lists.newArrayList();
        for (EndpointReferenceType tsf : tsfs) {
            TSFClient tsfClient = new TSFClient(tsf, clientConfiguration);
            TSSClient tssClient = null;
            if (tsfClient.getAccessibleTargetSystems().isEmpty()) {
                CreateTSRDocument in = CreateTSRDocument.Factory.newInstance();
                in.addNewCreateTSR();
                tssClient = tsfClient.createTSS(in);
            }
            if (tssClient == null) {
                // TODO: handle somehow admin privileges
                for (EndpointReferenceType tssEPR : tsfClient
                        .getAccessibleTargetSystems()) {
                    tssClient = new TSSClient(tssEPR, clientConfiguration);
                    break;
                }
            }
            tssClientsList.add(tssClient);
        }
        return tssClientsList;
    }

    public StorageClient getStorageFactoryClientSMS() {
        List<EndpointReferenceType> accessibleStorageFactoryServicesList = null;
        try {
            accessibleStorageFactoryServicesList = getRegistryClient()
                    .listAccessibleServices(StorageFactory.SMF_PORT);
        } catch (Exception e) {
            logger.error(
                    "Problem getting accessible StorageFactory services from registry!",
                    e);
            return null;
        }
        if (accessibleStorageFactoryServicesList.isEmpty()) {
            logger.error("No accessible StorageFactory service!");
            return null;
        }

        int i = new Random().nextInt(accessibleStorageFactoryServicesList
                .size());
        EndpointReferenceType storageFactoryEPR = accessibleStorageFactoryServicesList
                .get(i);

        logger.info("Using Storage Factory: <"
                + storageFactoryEPR.getAddress().getStringValue()
                + "> by user: <" + Session.getCurrent().getUser().getUsername()
                + ">");
        // TODO: check if setReceiver is needed?

        StorageClient storageClient = null;
        try {
            StorageFactoryClient sfc = new StorageFactoryClient(
                    storageFactoryEPR, clientConfiguration);
            storageClient = sfc.createSMS();
            logger.info("Created SMS: <"
                    + storageClient.getEPR().getAddress().getStringValue()
                    + "> by user <"
                    + Session.getCurrent().getUser().getUsername() + ">");
        } catch (Exception e) {
            logger.error("Problem with creating new StorageClient instance!", e);
            return null;
        }
        return storageClient;
    }

    public RegistryClient getRegistryClient() {
        return registryClient;
    }

    public IServiceOrchestrator getServiceOrchestratorClient() {
        return initializeServiceClient(IServiceOrchestrator.class,
                IServiceOrchestrator.PORT);
    }

    //    public IResourceBroker getResourceBrokerClient() {
//        return initializeServiceClient(IResourceBroker.class,
//                IResourceBroker.PORT);
//    }
//
    protected <T> T initializeServiceClient(Class<T> clazz, QName port) {
        List<EndpointReferenceType> accessibleServicesList = Lists
                .newArrayList();
        try {
            accessibleServicesList.addAll(registryClient
                    .listAccessibleServices(port));
        } catch (Exception e) {
            logger.warn("Problem with listing accessible services of class <"
                    + clazz.getCanonicalName() + ">", e);
            return null;
        }
        if (accessibleServicesList.isEmpty()) {
            logger.warn("No service of class <" + clazz.getCanonicalName()
                    + "> in registry!");
            return null;
        }
        // FIXME: always takes the first one
        EndpointReferenceType serviceEPR = accessibleServicesList.get(0);

        logger.info("Using service: <" + serviceEPR.getAddress().getStringValue()
                + "> by user: <" + Session.getCurrent().getUser().getUsername()
                + ">");
        String receiverDN = WSUtilities.extractServerIDFromEPR(serviceEPR);
        if (receiverDN != null) {
            this.clientConfiguration.getETDSettings().setReceiver(
                    new X500Principal(receiverDN));
        }

        try {
            return new UnicoreWSClientFactory(this.clientConfiguration)
                    .createPlainWSProxy(clazz, serviceEPR.getAddress()
                            .getStringValue());
        } catch (MalformedURLException e) {
            logger.error("Wrong URL during creation of WS proxy client!", e);
            return null;
        }
    }

    private RegistryClient createRegistryClient(String registryURL) {
        logger.info("Using registry: <" + registryURL + "> by user: <"
                + Session.getCurrent().getUser().getUsername() + ">");
        EndpointReferenceType registryEPR = EndpointReferenceType.Factory
                .newInstance();
        registryEPR.addNewAddress().setStringValue(registryURL);
        try {
            return new RegistryClient(registryEPR, clientConfiguration);
        } catch (Exception e) {
            return null;
        }
    }
}
