package pl.plgrid.unicore.common.exceptions;

public class UnavailableGridServiceException extends Exception {

    public UnavailableGridServiceException(String msg) {
        super(msg);
    }

    public UnavailableGridServiceException(String msg, Exception e) {
        super(msg, e);
    }
}
