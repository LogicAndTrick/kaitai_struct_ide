package io.kaitai.struct.runtime.results;

import io.kaitai.struct.runtime.KProperty;
import io.kaitai.struct.runtime.types.IType;

import java.util.ArrayList;

/**
 * An array of objects
 */
public class ArrayValue extends Value {
    protected KProperty property;
    protected ArrayList<Value> values;

    public ArrayValue(ContainerValue owner, KProperty property) {
        this.owner = owner;
        this.property = property;
        this.values = new ArrayList<>();
    }

    public int getSize() {
        return this.values.size();
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public IType getType() {
        return property.getType();
    }

    @Override
    public Object getValue() {
        return values;
    }

    @Override
    public Value getValue(String name) {
        return null;
    }

    @Override
    public Value getValueAtIndex(Value index) {
        int n = ((Number) index.getValue()).intValue();
        return values.get(n);
    }

    @Override
    public ArrayList<Value> getValues() {
        return this.values;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isStream() {
        return false;
    }

    public void add(Value value) {
        value.setArrayIndex(values.size());
        this.values.add(value);
    }
}
