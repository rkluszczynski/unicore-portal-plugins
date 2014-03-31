package pl.plgrid.unicore.common;

import pl.plgrid.unicore.common.resources.AvailableResource;

import java.util.Collection;


public interface GridResourcesExplorer {

    Collection<AvailableResource> getResources();

    Collection<Object> getJobs();

    Collection<Object> getStorages();

}
