package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;

/**
 * The kaitai stream type represented by _io
 */
public class StreamType implements IType {

    public final static StreamType INSTANCE = new StreamType();

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return "KaitaiStream";
    }

    @Override
    public String getDisplayName() {
        return "stream";
    }
}
