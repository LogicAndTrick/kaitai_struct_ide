package io.kaitai.struct.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * A kaitai enum object.
 */
public class KEnum {
    private String name;
    private HashMap<Long, String> values;

    public KEnum(String name) {
        this.name = name;
        this.values = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void add(long value, String name) {
        this.values.put(value, name);
    }

    public long getValue(String name) {
        for (Map.Entry<Long, String> entry : values.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        throw new IndexOutOfBoundsException(name + " is not a member of enum " + this.name);
    }

    public String getName(long value) {
        return values.get(value);
    }

    public KEnumMember getEnumValue(long value) {
        return new KEnumMember(name, getName(value));
    }
    public KEnumMember getEnumValue(String name) {
        return new KEnumMember(this.name, name);
    }
}
