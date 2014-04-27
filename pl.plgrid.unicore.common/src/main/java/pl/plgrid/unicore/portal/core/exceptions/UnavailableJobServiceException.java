package pl.plgrid.unicore.portal.core.exceptions;

public class UnavailableJobServiceException extends UnavailableGridServiceException {
    public UnavailableJobServiceException(String msg) {
        super(msg);
    }

    public UnavailableJobServiceException(String msg, Exception e) {
        super(msg, e);
    }
}
