package pl.plgrid.unicore.common.impl;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import de.fzj.unicore.uas.client.TSSClient;
import eu.unicore.portal.core.Session;
import org.apache.log4j.Logger;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.GridResourcesExplorer;
import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.common.entities.AtomicJobEntity;
import pl.plgrid.unicore.common.entities.StorageEntity;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.common.resources.AvailableResource;
import pl.plgrid.unicore.common.services.GlobalStorageService;
import pl.plgrid.unicore.common.services.StorageFactoryService;
import pl.plgrid.unicore.common.services.TargetSystemService;
import pl.plgrid.unicore.common.utils.ResourcesHelper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.unigrids.x2006.x04.services.tss.TargetSystemPropertiesDocument.TargetSystemProperties;
import static pl.plgrid.unicore.common.entities.StorageEntityType.SITE_STORAGE;

public class GridResourcesExplorerImpl implements GridResourcesExplorer {
    private static final Logger logger = Logger.getLogger(GridResourcesExplorerImpl.class);

    private final GridServicesExplorer gridServicesExplorer;
    private final TargetSystemService targetSystemService;
    private final GlobalStorageService globalStorageService;
    private final StorageFactoryService storageFactoryService;

    private List<TSSClient> tssClients;

    public GridResourcesExplorerImpl() {
        gridServicesExplorer =
                Session.getCurrent().getServiceRegistry().getService(GridServicesExplorer.class);
        targetSystemService = gridServicesExplorer.getTargetSystemService();
        globalStorageService = gridServicesExplorer.getGlobalStorageService();
        storageFactoryService = gridServicesExplorer.getStorageFactoryService();
        try {
            tssClients = targetSystemService.createClient();
        } catch (UnavailableGridServiceException e) {
            logger.error("Error getting TSS clients", e);
            tssClients = Lists.newArrayList();
        }
    }

    @Override
    public Collection<AvailableResource> getResources() {
        Map<String, AvailableResource> availableResourceMap = Maps.newHashMap();
        for (TSSClient tssClient : tssClients) {
            List<AvailableResource> tssAvailableResources = Lists.newArrayList();
            try {
                TargetSystemProperties targetSystemProperties = tssClient
                        .getResourcePropertiesDocument()
                        .getTargetSystemProperties();

                tssAvailableResources.addAll(
                        ResourcesHelper.convertAvailableResources(
                                targetSystemProperties
                                        .getAvailableResourceArray()
                        )
                );
                tssAvailableResources.addAll(
                        ResourcesHelper.convertStandardResources(
                                targetSystemProperties
                        )
                );

                ResourcesHelper.mergeTargetSystemResources(availableResourceMap, tssAvailableResources);

            } catch (Exception e) {
                logger.error("Error getting properties from tss client: " + tssClient
                        .getEPR()
                        .getAddress()
                        .getStringValue(), e);
            }
        }
        return availableResourceMap.values();
    }

    @Override
    public Collection<AtomicJobEntity> getJobs() {
        List<AtomicJobEntity> jobEprs = Lists.newArrayList();
        for (TSSClient tssClient : tssClients) {
            try {
                List<EndpointReferenceType> jobs = tssClient.getJobs();
                jobEprs.addAll(
                        Collections2.transform(jobs, new Function<EndpointReferenceType, AtomicJobEntity>() {
                            @Override
                            public AtomicJobEntity apply(EndpointReferenceType jobEpr) {
                                return new AtomicJobEntity(jobEpr);
                            }
                        })
                );
            } catch (Exception e) {
                logger.warn("Problems with getting jobs from: " + tssClient.getEPR().getAddress().getStringValue(),
                        e);
            }
        }
        return jobEprs;
    }

    @Override
    public Collection<StorageEntity> getStorages() {
        List<StorageEntity> storageClients = Lists.newArrayList();

        Collection<StorageEntity> globalStorages = getGlobalStorages();
        if (globalStorages != null && !globalStorages.isEmpty()) {
            storageClients.addAll(globalStorages);
        }

        Collection<StorageEntity> siteStorages = getSiteStorages();
        if (siteStorages != null && !siteStorages.isEmpty()) {
            storageClients.addAll(siteStorages);
        }

        Collection<StorageEntity> factoryStorages = getFactoryStorages();
        if (factoryStorages != null && !factoryStorages.isEmpty()) {
            storageClients.addAll(factoryStorages);
        }
        return storageClients;
    }

    @Override
    public Collection<StorageEntity> getGlobalStorages() {
        try {
            return globalStorageService.getStorageEntities();
        } catch (UnavailableGridServiceException e) {
            logger.error("Unable to retrieve global storage EPRs", e);
        }
        return null;
    }

    @Override
    public Collection<StorageEntity> getSiteStorages() {
        List<StorageEntity> storageClients = Lists.newArrayList();
        for (TSSClient tssClient : tssClients) {
            try {
                List<EndpointReferenceType> tssClientStorageEprs = tssClient.getStorages();
                storageClients.addAll(
                        Collections2.transform(tssClientStorageEprs, new Function<EndpointReferenceType, StorageEntity>() {
                            @Override
                            public StorageEntity apply(EndpointReferenceType storageEpr) {
                                return new StorageEntity(storageEpr, SITE_STORAGE);
                            }
                        })
                );
            } catch (Exception e) {
                logger.error("Error getting storages from tss client: " + tssClient.getEPR().getAddress().getStringValue());
            }
        }
        return storageClients;
    }

    @Override
    public Collection<StorageEntity> getFactoryStorages() {
        try {
            return storageFactoryService.getStorageEntities();
        } catch (UnavailableGridServiceException e) {
            logger.error("Unable to retrieve factory storages!", e);
        }
        return null;
    }
}
