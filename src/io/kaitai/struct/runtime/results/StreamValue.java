package io.kaitai.struct.runtime.results;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.types.IType;
import io.kaitai.struct.runtime.types.StreamType;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A kaitai stream value (represents _io)
 */
public class StreamValue extends Value {

    private KObject object;
    private KaitaiStream stream;

    public StreamValue(ContainerValue owner, KObject object, KaitaiStream stream) {
        this.owner = owner;
        this.object = object;
        this.stream = stream;
    }

    @Override
    public String getName() {
        return "_io";
    }

    @Override
    public IType getType() {
        return StreamType.INSTANCE;
    }

    @Override
    public Object getValue() {
        return stream;
    }

    @Override
    public Value getValue(String name) {
        if (name.equals("size")) {
            try {
                return new IntValue(owner, stream.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new UnsupportedOperationException("Cannot get " + name + " from " + getClass().getName() + ".");
    }

    @Override
    public ArrayList<Value> getValues() {
        return null;
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
        return false;
    }

    @Override
    public boolean isStream() {
        return true;
    }
}
