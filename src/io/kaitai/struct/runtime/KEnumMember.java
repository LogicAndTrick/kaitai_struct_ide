package io.kaitai.struct.runtime;

/**
 * A member of an enum object
 */
public class KEnumMember {
    private String enumName;
    private String value;

    public KEnumMember(String enumName, String value) {
        this.enumName = enumName;
        this.value = value;
    }

    public String getEnumName() {
        return enumName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KEnumMember enumValue = (KEnumMember) o;

        if (!enumName.equals(enumValue.enumName)) return false;
        return value.equals(enumValue.value);

    }

    @Override
    public int hashCode() {
        int result = enumName.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return enumName + "::" + value;
    }
}
