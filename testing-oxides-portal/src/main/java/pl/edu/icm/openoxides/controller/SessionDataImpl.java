package pl.edu.icm.openoxides.controller;

import java.util.UUID;

/**
 * Created by Rafal on 2015-03-27.
 */
public class SessionDataImpl implements SessionData {
    private String id = UUID.randomUUID().toString();

    @Override
    public String getId() {
        return id;
    }
}
