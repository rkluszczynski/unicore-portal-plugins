package pl.plgrid.unicore.common.resources;

import eu.unicore.portal.core.GlobalState;
import pl.plgrid.unicore.common.i18n.SiteResourcesI18N;

/**
 * Enumeration of well known names of Grid resources, defined in standards (JSDL).
 *
 * @author K. Benedyczak
 */
public enum StandardResources {
    individualCPUSpeed("JSDLUtils.CPU_speed"),
    cpuArchitecture("JSDLUtils.CPU_architecture"),
    individualCPUTime("JSDLUtils.CPU_time"),
    individualNumberOfCPUs("JSDLUtils.CPU_per_node"),
    individualPhysicalMemory("JSDLUtils.RAM"),
    osType("JSDLUtils.OS"),
    totalNumberOfCPUs("JSDLUtils.total_CPUs"),
    totalNumberOfNodes("JSDLUtils.total_nodes");

    private String msgKey;

    StandardResources(String msgKey) {
        this.msgKey = msgKey;
    }

    public String readableName() {
        return GlobalState.getMessage(SiteResourcesI18N.ID, msgKey);
    }

    public String description() {
        return GlobalState.getMessage(SiteResourcesI18N.ID, msgKey + ".description");
    }
}
