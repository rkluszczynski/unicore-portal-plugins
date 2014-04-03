package pl.plgrid.unicore.common.exceptions;

public class UnavailableJobServiceException extends UnavailableGridServiceException {
    public UnavailableJobServiceException(String msg) {
        super(msg);
    }

    public UnavailableJobServiceException(String msg, Exception e) {
        super(msg, e);
    }
}
