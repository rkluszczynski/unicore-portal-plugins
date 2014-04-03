package pl.plgrid.unicore.common;

import de.fzj.unicore.uas.client.StorageClient;
import pl.plgrid.unicore.common.resources.AvailableResource;

import java.util.Collection;


public interface GridResourcesExplorer {

    Collection<AvailableResource> getResources();

    Collection<Object> getJobs();

    Collection<StorageClient> getStorages();

    Collection<StorageClient> getGlobalStorages();

    Collection<StorageClient> getSiteStorages();

    Collection<StorageClient> getFactoryStorages();

}
