package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.exprlang.Ast;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.expressions.Evaluator;
import io.kaitai.struct.runtime.results.BinaryValue;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;

/**
 * An array of bytes. Just a blob.
 */
public class ByteArray implements IType {

    private boolean isEos;
    private Ast.expr size;

    public ByteArray() {
        this.isEos = true;
    }

    public ByteArray(Ast.expr size) {
        this.size = size;
    }

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        long count;
        if (isEos) {
            count = stream.size() - stream.pos();
        } else {
            count = Evaluator.evaluate(size, stream, parent).longValue();
        }
        return new BinaryValue(parent, stream.readBytes(count));
    }

    @Override
    public String getName() {
        return "byte[]";
    }

    @Override
    public String getDisplayName() {
        return "byte array";
    }
}
