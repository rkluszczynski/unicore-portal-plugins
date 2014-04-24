package pl.plgrid.unicore.portal.core.utils;

import com.vaadin.server.ClassResource;
import com.vaadin.server.DownloadStream;

/**
 * Helper class for handling data included in JAR plugin.
 *
 * @author R.Kluszczynski
 */
public class ClassPathResource extends ClassResource {
    private String resourceName;

    public ClassPathResource(String resourceName) {
        super(resourceName);
        this.resourceName = resourceName;
    }

    public ClassPathResource(Class<?> associatedClass, String resourceName) {
        super(associatedClass, resourceName);
        this.resourceName = resourceName;
    }

    @Override
    public DownloadStream getStream() {
        final DownloadStream downloadStream = new DownloadStream(
                ClassLoader.getSystemResourceAsStream(resourceName), getMIMEType(),
                getFilename());
        downloadStream.setBufferSize(getBufferSize());
        downloadStream.setCacheTime(getCacheTime());
        return downloadStream;
    }
}
