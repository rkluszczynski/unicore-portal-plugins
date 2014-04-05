package pl.plgrid.unicore.common.model;

import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class AtomicJobModel {
    private String applicationName = "";
    private String applicationVersion = "";

    private final Map<String, String> parametersMap = Maps.newConcurrentMap();
    private final Set<String> inputFileSet = Sets.newHashSet();
    private final Set<String> outputFileSet = Sets.newHashSet();

    private final Map<String, String> resourceSet = Maps.newConcurrentMap();


    public Map<String, String> getResourceSet() {
        return resourceSet;
    }
}
