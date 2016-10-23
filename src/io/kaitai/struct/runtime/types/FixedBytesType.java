package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.*;

import java.io.IOException;
import java.util.Arrays;

/**
 * An array of bytes with a fixed value.
 */
public class FixedBytesType implements IType {

    private byte[] contents;

    public FixedBytesType(byte[] contents) {

        this.contents = contents;
    }

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        byte[] bytes = stream.readBytes(contents.length);
        if (Arrays.equals(bytes, contents)) {
             return new BinaryValue(parent, bytes);
        } else {
            byte[] err = new byte[bytes.length + 4];
            err[0] = err[1] = err[err.length - 1] = err[err.length - 2] = '!';
            System.arraycopy(bytes, 0, err, 2, bytes.length);
            return new BinaryValue(parent, err);
        }
    }

    @Override
    public String getName() {
        return "byte[]";
    }

    @Override
    public String getDisplayName() {
        return "fixed value";
    }
}
