package pl.plgrid.unicore.common.resources;

/**
 * Can be only set to true or false
 *
 * @author K. Benedyczak
 */
public class AvailableBooleanResource extends AvailableResource {
    public AvailableBooleanResource(String name, String description, String defaultValue) {
        super(name, description, defaultValue);
    }

    @Override
    public void validate(String value) {
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value))
            throw new IllegalArgumentException("The value must be true or false");
    }

    @Override
    public void mergeWith(AvailableResource res) {
        //nothing: valid values space is constant
    }
}
