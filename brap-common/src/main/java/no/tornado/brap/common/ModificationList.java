package no.tornado.brap.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ModificationList implements Serializable {

    private static final long serialVersionUID = 4074710580963411799L;

    private Map<String, Object> modifiedProperties = new HashMap<String, Object>();

    public void addModification(String key, Object value) {
        modifiedProperties.put(key, value);
    }

    public Map<String, Object> getModifiedProperties() {
        return modifiedProperties;
    }

    public void setModifiedProperties(Map<String, Object> modifiedProperties) {
        this.modifiedProperties = modifiedProperties;
    }
}