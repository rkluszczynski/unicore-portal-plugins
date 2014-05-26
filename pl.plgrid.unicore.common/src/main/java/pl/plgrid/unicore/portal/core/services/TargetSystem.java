package pl.plgrid.unicore.portal.core.services;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import de.fzj.unicore.uas.TargetSystemFactory;
import de.fzj.unicore.uas.client.TSFClient;
import de.fzj.unicore.uas.client.TSSClient;
import org.unigrids.x2006.x04.services.tsf.CreateTSRDocument;
import org.unigrids.x2006.x04.services.tss.TargetSystemPropertiesDocument.TargetSystemProperties;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import pl.plgrid.unicore.common.resources.AvailableResource;
import pl.plgrid.unicore.common.utils.ResourcesHelper;
import pl.plgrid.unicore.portal.core.entities.AtomicJobEntity;
import pl.plgrid.unicore.portal.core.entities.StorageEntity;
import pl.plgrid.unicore.portal.core.entities.TSSClientEntity;
import pl.plgrid.unicore.portal.core.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.portal.core.utils.SecurityHelper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static pl.plgrid.unicore.portal.core.entities.StorageEntityType.FACTORY_STORAGE;

/**
 * Created by Rafal on 2014-05-25.
 */
public class TargetSystem extends AbstractStorage {
    private final List<TSSClientEntity> tssClientEntities = Lists.newArrayList();
    private Collection<AtomicJobEntity> gridJobsEntities;


    public TargetSystem() {
        try {
            tssClientEntities.addAll(getTSSClients());
        } catch (UnavailableGridServiceException e) {
            logger.warn("Problem with tssClientEntities initialization", e);
        }
    }


    @Override
    public Collection<StorageEntity> getStorageEntities() throws UnavailableGridServiceException {
        List<StorageEntity> storageEntities = Lists.newArrayList();

        for (TSSClientEntity tssClientEntity : tssClientEntities) {
            try {
                List<EndpointReferenceType> storageList = tssClientEntity
                        .getTSSClient()
                        .getStorages();

                storageEntities.addAll(
                        Collections2.transform(storageList,
                                new Function<EndpointReferenceType, StorageEntity>() {
                                    @Override
                                    public StorageEntity apply(EndpointReferenceType storageEpr) {
                                        return new StorageEntity(storageEpr, FACTORY_STORAGE);
                                    }
                                }
                        )
                );

            } catch (Exception e) {
                logger.error(String.format("Problem during getting storages from %s",
                        tssClientEntity
                                .getTssClientEpr()
                                .getAddress()
                                .getStringValue()
                ), e);
            }
        }
        return storageEntities;
    }


    public Collection<AvailableResource> getGridResources() {
        Map<String, AvailableResource> availableResourceMap = Maps.newHashMap();

        for (TSSClientEntity tssClientEntity : tssClientEntities) {
            List<AvailableResource> tssAvailableResources = Lists.newArrayList();
            try {
                TargetSystemProperties targetSystemProperties = tssClientEntity
                        .getTssProperties();

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
                logger.error("Error getting properties from tss client: " + tssClientEntity
                        .getTssClientEpr()
                        .getAddress()
                        .getStringValue(), e);
            }
        }
        return availableResourceMap.values();
    }


    private List<TSSClientEntity> getTSSClients() throws UnavailableGridServiceException {
        List<EndpointReferenceType> accessibleServices = getAccessibleServices(TargetSystemFactory.TSF_PORT);
        List<TSSClientEntity> tssClients = Lists.newArrayList();

        for (EndpointReferenceType accessibleService : accessibleServices) {
            TSFClient tsfClient = null;
            try {
                tsfClient = new TSFClient(accessibleService, SecurityHelper.getClientConfig());
                TSSClient tssClient = null;
                if (tsfClient.getAccessibleTargetSystems().isEmpty()) {
                    CreateTSRDocument in = CreateTSRDocument.Factory.newInstance();
                    in.addNewCreateTSR();
                    tssClient = tsfClient.createTSS(in);
                }

                if (tssClient == null) {
                    // TODO: handle somehow admin privileges
                    for (EndpointReferenceType tssEpr : tsfClient
                            .getAccessibleTargetSystems()) {
                        tssClients.add(
                                new TSSClientEntity(tssEpr)
                        );
                        logger.info("Added user's TSS epr: " + tssEpr.getAddress().getStringValue());
                        break;
                    }
                }
                logger.info("Processed TSF epr: " + accessibleService.getAddress().getStringValue());
            } catch (Exception ex) {
                String message = ((tsfClient == null)
                        ? "Problem with getting TSF client for EPR: "
                        : "Problem with creating TSS client for TSF: ")
                        + accessibleService.getAddress().getStringValue();
                logger.warn(message, ex);
            }
        }

        if (tssClients.isEmpty()) {
            throw new UnavailableGridServiceException("No target system services for user");
        }
        return tssClients;
    }


    public Collection<AtomicJobEntity> getGridJobsEntities() {
        List<AtomicJobEntity> atomicJobEntities = Lists.newArrayList();

        for (TSSClientEntity tssClientEntity : tssClientEntities) {
            try {
                List<EndpointReferenceType> jobsList = tssClientEntity
                        .getTSSClient()
                        .getJobs();

                atomicJobEntities.addAll(
                        Collections2.transform(jobsList,
                                new Function<EndpointReferenceType, AtomicJobEntity>() {
                                    @Override
                                    public AtomicJobEntity apply(EndpointReferenceType jobEpr) {
                                        return new AtomicJobEntity(jobEpr);
                                    }
                                }
                        )
                );

            } catch (Exception e) {
                logger.error(String.format("Problem during getting storages from %s",
                        tssClientEntity
                                .getTssClientEpr()
                                .getAddress()
                                .getStringValue()
                ), e);
            }
        }
        return atomicJobEntities;
    }
}
