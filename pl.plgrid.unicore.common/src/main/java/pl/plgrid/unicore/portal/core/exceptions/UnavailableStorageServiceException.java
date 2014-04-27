package pl.plgrid.unicore.portal.core.exceptions;

public class UnavailableStorageServiceException extends UnavailableGridServiceException {
    public UnavailableStorageServiceException(String msg) {
        super(msg);
    }

    public UnavailableStorageServiceException(String msg, Exception e) {
        super(msg, e);
    }
}
