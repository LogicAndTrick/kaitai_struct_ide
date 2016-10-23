package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.SubKaitaiStream;
import io.kaitai.struct.exprlang.Ast;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.expressions.Evaluator;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A stream which will create a substream of a fixed size and then read a user type from it.
 */
public class SizedUserType extends UserType {

    private boolean eos;
    private Ast.expr size;

    public SizedUserType(ArrayList<String> names, Ast.expr size, boolean eos) {
        super(names);
        this.size = size;
        this.eos = eos;
    }


    @Override
    public KaitaiStream createStream(KaitaiStream stream, ContainerValue context) throws IOException {
        long size = eos ? stream.pos() - stream.size() : Evaluator.evaluate(this.size, stream, context).longValue();
        try {
            long pos = stream.pos();
            return new SubKaitaiStream(stream, pos, pos + size);
        } catch (IOException e) {
            return stream;
        }
    }

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        long size = eos ? stream.pos() - stream.size() : Evaluator.evaluate(this.size, stream, parent).longValue();

        Value result = super.read(object, stream, read, parent);

        stream.seek(size);

        return result;
    }
}
