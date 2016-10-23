package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A user-specific type (an object)
 */
public class UserType implements IType {

    private ArrayList<String> names;

    public UserType(ArrayList<String> names) {
        this.names = names;
    }

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        String name = this.names.get(this.names.size() - 1);
        KObject type = object.findType(name);
        return type.read(stream, read, parent);
    }

    @Override
    public String getName() {
        return String.join(".", (CharSequence[]) this.names.toArray(new String[0]));
    }

    @Override
    public String getDisplayName() {
        return this.names.get(this.names.size() - 1);
    }
}
