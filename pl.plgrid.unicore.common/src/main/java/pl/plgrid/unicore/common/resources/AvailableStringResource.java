/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.plgrid.unicore.common.resources;

/**
 * String resources accept all values
 *
 * @author K. Benedyczak
 */
public class AvailableStringResource extends AvailableResource {
    public AvailableStringResource(String name, String description, String defaultValue) {
        super(name, description, defaultValue);
    }

    @Override
    public void validate(String value) {
    }

    @Override
    public void mergeWith(AvailableResource res) {
        //nop: valid values space is not restricted
    }
}
