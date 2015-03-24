package pl.edu.icm.openoxides.service.input;

import java.text.DateFormat;
import java.util.Date;

public class OxidesPortalData {
    private final String message;
    private final String createdAt;

    public OxidesPortalData(String message) {
        this.message = message;
        createdAt = DateFormat.getDateTimeInstance().format(new Date());
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}