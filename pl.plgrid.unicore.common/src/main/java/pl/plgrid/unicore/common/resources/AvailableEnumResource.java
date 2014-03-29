/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.plgrid.unicore.common.resources;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Enumerated string resource.
 *
 * @author K. Benedyczak
 */
public class AvailableEnumResource extends AvailableResource {
    private Set<String> allowed;

    public AvailableEnumResource(String name, String description, String defaultValue, String[] allowedValues) {
        super(name, description, defaultValue);
        allowed = new HashSet<String>();
        Collections.addAll(allowed, allowedValues);
    }

    @Override
    public void validate(String value) throws IllegalArgumentException {
        if (!allowed.contains(value))
            throw new IllegalArgumentException("The value " + value + " is not allowed. Allowed values: " +
                    allowed.toString());
    }

    public Set<String> getAllowed() {
        return allowed;
    }

    public void setAllowed(Set<String> allowed) {
        this.allowed = allowed;
    }

    @Override
    public void mergeWith(AvailableResource res) {
        if (!(res instanceof AvailableEnumResource))
            throw new IllegalArgumentException("Trying to merge non-enum " + res +
                    " resource with enum resource " + toString());
        AvailableEnumResource resC = (AvailableEnumResource) res;
        allowed.addAll(resC.allowed);
    }
}
