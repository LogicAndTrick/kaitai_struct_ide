package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.FloatValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;

/**
 * A floating point number
 */
public class FloatType implements IType {

    private int width;
    private boolean bigEndian;

    public FloatType(int width, boolean bigEndian) {
        if (width != 4 && width != 8) throw new IllegalArgumentException("Float width not supported: " + width);
        this.width = width;
        this.bigEndian = bigEndian;
    }

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        return new FloatValue(parent, readFloat(stream));
    }

    private Number readFloat(KaitaiStream stream) throws IOException {
        switch (width) {
            case 4: return bigEndian ? stream.readF4be() : stream.readF4le();
            case 8: return bigEndian ? stream.readF8be() : stream.readF8le();
        }
        throw new IllegalStateException("This float type is not a valid width.");
    }

    @Override
    public String getName() {
        return "f" + width + (bigEndian ? "be" : "le");
    }

    @Override
    public String getDisplayName() {
        return width == 4 ? "float" : "double";
    }
}
