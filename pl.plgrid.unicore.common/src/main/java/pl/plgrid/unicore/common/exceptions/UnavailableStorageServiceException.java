package pl.plgrid.unicore.common.exceptions;

public class UnavailableStorageServiceException extends UnavailableGridServiceException {
    public UnavailableStorageServiceException(String msg) {
        super(msg);
    }

    public UnavailableStorageServiceException(String msg, Exception e) {
        super(msg, e);
    }
}
