package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.IntValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;

/**
 * An integer number
 */
public class IntType implements IType {

    private int width;
    private boolean signed;
    private boolean bigEndian;

    public IntType(int width, boolean signed, boolean bigEndian) {
        if (width != 1 && width != 2 && width != 4 && width != 8) throw new IllegalArgumentException("Integer width not supported: " + width);
        this.width = width;
        this.signed = signed;
        this.bigEndian = bigEndian;
    }

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        return new IntValue(parent, readInteger(stream));
    }

    private Number readInteger(KaitaiStream stream) throws IOException {
        if (signed) {
            switch (width) {
                case 1: return stream.readS1();
                case 2: return bigEndian ? stream.readS2be() : stream.readS2le();
                case 4: return bigEndian ? stream.readS4be() : stream.readS4le();
                case 8: return bigEndian ? stream.readS8be() : stream.readS8le();
            }
        } else {
            switch (width) {
                case 1: return stream.readU1();
                case 2: return bigEndian ? stream.readU2be() : stream.readU2le();
                case 4: return bigEndian ? stream.readU4be() : stream.readU4le();
                case 8: return bigEndian ? stream.readU8be() : stream.readU8le();
            }
        }
        throw new IllegalStateException("This int type is not a valid width.");
    }

    @Override
    public String getName() {
        return (signed ? "s" : "u") + width + (bigEndian ? "be" : "le");
    }

    @Override
    public String getDisplayName() {
        switch (width) {
            case 1: return signed ? "sbyte" : "byte";
            case 2: return signed ? "short" : "ushort";
            case 4: return signed ? "int" : "uint";
            case 8: return signed ? "long" : "ulong";
        }
        return "int?";
    }
}
