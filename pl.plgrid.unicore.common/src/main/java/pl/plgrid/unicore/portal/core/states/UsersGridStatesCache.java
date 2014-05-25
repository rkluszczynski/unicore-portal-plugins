package pl.plgrid.unicore.portal.core.states;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import eu.unicore.portal.core.Session;
import eu.unicore.portal.core.threads.BackgroundWorker;
import eu.unicore.portal.core.threads.IProgressMonitor;

import java.util.List;
import java.util.Map;

public class UsersGridStatesCache {
    private final Map<String, UserGridState> userGridStates = Maps.newConcurrentMap();

    public UserGridState getUserGridState() {
        String userId = Session.getCurrent().getUser().getId();
        UserGridState userGridState;
        synchronized (userGridStates) {
            userGridState = userGridStates.get(userId);
        }
        if (userGridState == null || userGridState.isExpired()) {
            userGridState = new UserGridState();
            synchronized (userGridStates) {
                userGridStates.put(userId, userGridState);
            }
        } else {
            userGridState.setDirty();
        }

        new BackgroundWorker("") {
            @Override
            protected void work(IProgressMonitor progress) {
                List<String> expiredGridStates = Lists.newArrayList();
                for (Map.Entry<String, UserGridState> entry : userGridStates.entrySet()) {
                    if (entry.getValue().isExpired()) {
                        expiredGridStates.add(entry.getKey());
                    }
                }

                synchronized (userGridStates) {
                    for (String userId : expiredGridStates) {
                        userGridStates.remove(userId);
                    }
                }
            }
        }.schedule();

        return userGridState;
    }
}
