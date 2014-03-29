/*
 * Copyright (c) 2014 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.plgrid.unicore.common.resources;

/**
 * Resource with double value type. Range restricted.
 *
 * @author K. Benedyczak
 */
public class AvailableDoubleResource extends AvailableResource {
    private double min;
    private double max;

    public AvailableDoubleResource(String name, String description, String defaultValue, double min, double max) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public void validate(String value) {
        try {
            double v = Double.parseDouble(value);
            if (v < min)
                throw new IllegalArgumentException("Value can not be smaller then " + min);
            if (v > max)
                throw new IllegalArgumentException("Value can not be greater then " + max);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a floating point number", e);
        }
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    @Override
    public void mergeWith(AvailableResource res) {
        if (!(res instanceof AvailableDoubleResource))
            throw new IllegalArgumentException("Trying to merge non-double " + res +
                    " resource with double resource " + toString());
        AvailableDoubleResource resC = (AvailableDoubleResource) res;
        if (resC.min < min)
            min = resC.min;
        if (resC.max > max)
            max = resC.max;
    }
}
