package io.kaitai.struct.runtime.results;

import io.kaitai.struct.runtime.KProperty;
import io.kaitai.struct.runtime.types.CalcType;
import io.kaitai.struct.runtime.types.IType;

import java.util.ArrayList;

/**
 * A value of an enum
 */
public class EnumValue extends Value {

    protected Object value;
    private String enumName;
    private String valueName;

    public EnumValue(ContainerValue owner, String enumName, String valueName) {
        this.enumName = enumName;
        this.valueName = valueName;
        this.owner = owner;
        this.value = owner.getObject().findEnum(enumName).getEnumValue(valueName);
    }

    @Override
    public String getName() {
        return this.property == null ? "none" : this.property.getName();
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    public Value getValue(String name) {
        throw new UnsupportedOperationException("Cannot get " + name + " from " + getClass().getName() + ".");
    }

    @Override
    public IType getType() {
        return this.property == null ? CalcType.INSTANCE : this.property.getType();
    }

    @Override
    public ArrayList<Value> getValues() {
        return null;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isStream() {
        return false;
    }
}
