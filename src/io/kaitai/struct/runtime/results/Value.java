package io.kaitai.struct.runtime.results;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KProperty;
import io.kaitai.struct.runtime.types.IType;

import java.util.ArrayList;

/**
 * The base class for a value.
 */
public abstract class Value {

    protected KProperty property;
    protected KaitaiStream stream;
    protected ContainerValue owner;
    protected long startIndex;
    protected long endIndex;
    protected int arrayIndex = -1;

    /**
     * Get the index in the stream that this value started at
     */
    public long getStartIndex() {
        return startIndex;
    }

    /**
     * Set the index in the stream that this value started at
     */
    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Get the index in the stream that this value ended at
     */
    public long getEndIndex() {
        return endIndex;
    }

    /**
     * Set the index in the stream that this value ended at
     */
    public void setEndIndex(long endIndex) {
        this.endIndex = endIndex;
    }

    /**
     * Get the array index of this value, if it's in one
     */
    public int getArrayIndex() {
        return arrayIndex;
    }

    /**
     * Set the array index of this value
     */
    public void setArrayIndex(int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    /**
     * Get the property of this value
     */
    public KProperty getProperty() {
        return property;
    }

    /**
     * Set the property of this value
     */
    public void setProperty(KProperty property) {
        this.property = property;
    }

    /**
     * Get the name of this value
     */
    public abstract String getName();

    /**
     * Get the display name of this value (includes object hierarchy and array index)
     */
    public String getDisplayName() {
        String str = "";
        if (this.owner != null && this.owner.owner != null) {
            str += owner.getDisplayName() + ".";
        }
        str += getName();
        if (this.arrayIndex >= 0) {
            str += "[" + this.arrayIndex + "]";
        }
        return str;
    }

    /**
     * Get the type of this value
     */
    public abstract IType getType();

    /**
     * Get the raw value of this value
     */
    public abstract Object getValue();

    /**
     * Get a child value of this value (only works for containers)
     */
    public abstract Value getValue(String name);

    /**
     * Get a value at an index of this value (only works for arrays)
     */
    public Value getValueAtIndex(Value index) {
        throw new UnsupportedOperationException("Cannot access an index of " + this.getClass().getName() + ".");
    }

    /**
     * Get a list of all the values owned by this value (only works for containers and arrays)
     */
    public abstract ArrayList<Value> getValues();

    /**
     * Get the owner of this value. The root value will return null.
     */
    public ContainerValue getOwner() {
        return owner;
    }

    /**
     * Get the stream that this value came from
     */
    public KaitaiStream getStream() {
        return stream;
    }

    /**
     * Is this a primitive value?
     */
    public abstract boolean isPrimitive();

    /**
     * Is this a container (object)?
     */
    public abstract boolean isContainer();

    /**
     * Is this an array?
     */
    public abstract boolean isArray();

    /**
     * Is this a stream?
     */
    public abstract boolean isStream();
}
