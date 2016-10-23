package io.kaitai.struct.runtime.results;

import io.kaitai.struct.runtime.KProperty;
import io.kaitai.struct.runtime.types.CalcType;
import io.kaitai.struct.runtime.types.IType;

import java.util.ArrayList;

/**
 * Generic class for a primitive value
 */
public abstract class PrimitiveValue extends Value {

    protected Object value;

    protected PrimitiveValue(ContainerValue owner, Object value) {
        this.owner = owner;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.property == null ? "none" : this.property.getName();
    }

    public KProperty getProperty() {
        return this.property;
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    public Value getValue(String name) {
        throw new UnsupportedOperationException("Cannot get " + name + " from " + getClass().getName() + ".");
    }

    @Override
    public ArrayList<Value> getValues() {
        return null;
    }

    @Override
    public IType getType() {
        return this.property == null ? CalcType.INSTANCE : this.property.getType();
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
