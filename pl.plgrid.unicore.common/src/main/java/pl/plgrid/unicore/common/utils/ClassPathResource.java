package pl.plgrid.unicore.common.utils;

import com.vaadin.server.ClassResource;
import com.vaadin.server.DownloadStream;

/**
 * @author Rafal
 */
public class ClassPathResource extends ClassResource {
    private String resName;

    public ClassPathResource(String resourceName) {
        super(resourceName);
        this.resName = resourceName;
    }

    public ClassPathResource(Class<?> associatedClass, String resourceName) {
        super(associatedClass, resourceName);
        this.resName = resourceName;
    }

    @Override
    public DownloadStream getStream() {
        final DownloadStream ds = new DownloadStream(
                ClassLoader.getSystemResourceAsStream(resName), getMIMEType(),
                getFilename());
        ds.setBufferSize(getBufferSize());
        ds.setCacheTime(getCacheTime());
        return ds;
    }
}
