package com.iwitness.androidapp.controllers.adapters;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Root(name = "r")
public class KeyValuePairs  implements Serializable { //extends ArrayList<KeyValuePairs>

    @ElementList(inline = true, required = false)
    public List<KeyValuePairs> list = new ArrayList<KeyValuePairs>();

    @Attribute(name = "k", required = false)
    private String _key;
    @Attribute(name = "v", required = false)
    private String _displayName;

    public String getKey() {
        return _key;
    }

    public void setKey(String key) {
        _key = key;
    }

    public String getDisplayName() {
        return _displayName;
    }

    public void setDisplayName(String displayName) {
        _displayName = displayName;
    }

    public KeyValuePairs() {
        super();
    }

    public KeyValuePairs(@Attribute(name = "k") String key, @Attribute(name = "v")String displayName) {
        this._key = key;
        this._displayName = displayName;
    }

    public void toXml(String path) throws Exception {
        Serializer serializer = new Persister();
        File file = new File(path);
        serializer.write(this, file);
    }

    public KeyValuePairs getList(String key) {
        for(KeyValuePairs item : list) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Gets list of Keys
     * @return ArrayList<String>
     */
    public ArrayList<String> getKeys() {
        ArrayList<String> result = new ArrayList<String>();
        for (KeyValuePairs item : list) {
            result.add(item.getKey());
        }

        return result;
    }

    /**
     * Gets list of Values
     * @return ArrayList<String>
     */
    public ArrayList<String> getValues() {
        ArrayList<String> result = new ArrayList<String>();
        for (KeyValuePairs item : list) {
            result.add(item.getDisplayName());
        }

        return result;
    }
}
