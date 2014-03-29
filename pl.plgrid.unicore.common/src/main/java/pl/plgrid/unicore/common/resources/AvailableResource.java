/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.plgrid.unicore.common.resources;

/**
 * Base class representing a Grid resource offering. The resource is available in some context
 * which is defined by the method (and its arguments) which returned the instance.
 * <p/>
 * Concrete subclasses are used to provide typing.
 *
 * @author K. Benedyczak
 */
public abstract class AvailableResource {
    private String name;
    private String description;
    private String defaultValue;

    public AvailableResource(String name, String description, String defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public abstract void validate(String value) throws IllegalArgumentException;

    public abstract void mergeWith(AvailableResource res);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return name + " (" + description + "), default value: " + defaultValue;
    }
}
