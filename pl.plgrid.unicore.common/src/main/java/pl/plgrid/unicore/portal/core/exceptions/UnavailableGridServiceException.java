package pl.plgrid.unicore.portal.core.exceptions;

public class UnavailableGridServiceException extends Exception {

    public UnavailableGridServiceException(String msg) {
        super(msg);
    }

    public UnavailableGridServiceException(String msg, Exception e) {
        super(msg, e);
    }
}
