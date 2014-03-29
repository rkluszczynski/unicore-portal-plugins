/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.plgrid.unicore.common.resources;

/**
 * Resource with integer value type. Range restricted.
 *
 * @author K. Benedyczak
 */
public class AvailableIntResource extends AvailableResource {
    private int min;
    private int max;

    public AvailableIntResource(String name, String description, String defaultValue, int min, int max) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public void validate(String value) {
        try {
            int v = Integer.parseInt(value);
            if (v < min)
                throw new IllegalArgumentException("Value can not be smaller then " + min);
            if (v > max)
                throw new IllegalArgumentException("Value can not be greater then " + max);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not an integer number", e);
        }
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public void mergeWith(AvailableResource res) {
        if (!(res instanceof AvailableIntResource))
            throw new IllegalArgumentException("Trying to merge non-int " + res +
                    " resource with int resource " + toString());
        AvailableIntResource resC = (AvailableIntResource) res;
        if (resC.min < min)
            min = resC.min;
        if (resC.max > max)
            max = resC.max;
    }
}
