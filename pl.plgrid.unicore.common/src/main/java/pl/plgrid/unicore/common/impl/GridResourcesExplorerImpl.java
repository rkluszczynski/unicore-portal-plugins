package pl.plgrid.unicore.common.impl;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import de.fzj.unicore.uas.client.TSSClient;
import eu.unicore.portal.core.Session;
import org.apache.log4j.Logger;
import pl.plgrid.unicore.common.GridResourcesExplorer;
import pl.plgrid.unicore.common.GridServicesExplorer;
import pl.plgrid.unicore.common.exceptions.UnavailableGridServiceException;
import pl.plgrid.unicore.common.resources.AvailableResource;
import pl.plgrid.unicore.common.services.TargetSystemService;
import pl.plgrid.unicore.common.utils.ResourcesHelper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.unigrids.x2006.x04.services.tss.TargetSystemPropertiesDocument.TargetSystemProperties;

public class GridResourcesExplorerImpl implements GridResourcesExplorer {
    private static final Logger logger = Logger.getLogger(GridResourcesExplorerImpl.class);

    private final GridServicesExplorer gridServicesExplorer;
    private final TargetSystemService targetSystemService;
    private List<TSSClient> tssClients;

    public GridResourcesExplorerImpl() {
        gridServicesExplorer =
                Session.getCurrent().getServiceRegistry().getService(GridServicesExplorer.class);
        targetSystemService = gridServicesExplorer.getTargetSystemService();
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
    public Collection<Object> getJobs() {
        return null;
    }

    @Override
    public Collection<Object> getStorages() {
        return null;
    }
}
