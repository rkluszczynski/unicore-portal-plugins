package pl.edu.icm.openoxides.saml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseAttributes {
    private Map<String, List<String>> attributes = new HashMap();

    public void put(String name, String value) {
        if (attributes.get(name) == null) {
            attributes.put(name, new ArrayList());
        }
        attributes.get(name).add(value);
    }

    public void put(String name, List<String> values) {
        if (attributes.get(name) == null) {
            attributes.put(name, new ArrayList());
        }
        attributes.get(name).addAll(values);
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void merge(ResponseAttributes other) {
        for (Map.Entry<String, List<String>> entry : other.getAttributes().entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public boolean containsKey(String name) {
        return attributes.containsKey(name);
    }
}
