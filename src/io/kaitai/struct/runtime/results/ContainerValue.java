package io.kaitai.struct.runtime.results;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.types.IType;
import io.kaitai.struct.runtime.types.UserType;

import java.util.ArrayList;

/**
 * An object (key-value map)
 */
public class ContainerValue extends Value {

    private KObject object;
    private ArrayList<Value> values;

    public ContainerValue(ContainerValue owner, KaitaiStream stream, KObject object) {
        this.owner = owner;
        this.stream = stream;
        this.object = object;
        this.values = new ArrayList<>();

        this.add(new StreamValue(this, object, stream));
    }

    public void add(Value value) {
        this.values.add(value);
    }

    public void remove(Value value) {
        this.values.remove(value);
    }

    @Override
    public String getName() {
        return property == null ? "_root" : this.property.getName();
    }

    public KObject getObject() {
        return this.object;
    }

    @Override
    public IType getType() {
        ArrayList<String> n = new ArrayList<>();
        n.add(this.object.getName());
        return new UserType(n);
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isStream() {
        return false;
    }

    @Override
    public Value getValue(String name) {
        if (name.equals("_parent")) return owner;
        if (name.equals("_root")) return getRoot();
        return this.values.stream().filter(x -> name.equals(x.getName())).findFirst().orElse(new NullValue(this));
    }

    @Override
    public ArrayList<Value> getValues() {
        return this.values;
    }

    public Value getRoot() {
        ContainerValue root = this;
        while (root.owner != null) root = root.owner;
        return root;
    }
}
