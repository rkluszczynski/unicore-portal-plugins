package pl.plgrid.unicore.portal.core.states;

import pl.plgrid.unicore.portal.core.services.GlobalStorage;

import java.util.Collection;

/**
 * Created by Rafal on 2014-05-25.
 */
public class UserGridState {
    private static final long TTL = 2L * 60L * 60L * 1000L;

    private long lastAccessMillis = System.currentTimeMillis();


    public UserGridState() {
    }


    public boolean isExpired() {
        return System.currentTimeMillis() > lastAccessMillis + TTL;
    }

    public void setDirty() {
        lastAccessMillis = System.currentTimeMillis();
    }

    public Collection<GlobalStorage> getGlobalStorageServices() {
    }
}
