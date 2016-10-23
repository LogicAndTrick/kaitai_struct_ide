package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.exprlang.Ast;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.expressions.Evaluator;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;
import io.kaitai.struct.runtime.results.StringValue;

import java.io.IOException;

/**
 * A string, terminated or otherwise.
 */
public class StringType implements IType {

    private String encoding;
    private boolean isFixedLenth;
    private boolean isEos;

    private Ast.expr length;

    private int terminator;
    private boolean includeTerminator;
    private boolean consumeTerminator;

    public StringType(String encoding, Ast.expr length) {
        this.encoding = encoding;
        this.length = length;
        this.isFixedLenth = true;
    }

    public StringType(String encoding, int terminator, boolean includeTerminator, boolean consumeTerminator) {
        this.encoding = encoding;
        this.terminator = terminator;
        this.includeTerminator = includeTerminator;
        this.consumeTerminator = consumeTerminator;
        this.isFixedLenth = false;
    }

    public StringType(String encoding) {
        this.encoding = encoding;
        this.isEos = true;
    }

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        if (isEos) {
            String str = stream.readStrEos(encoding);
            return new StringValue(parent, str);
        } else if (isFixedLenth) {
            String str = stream.readStrByteLimit(Evaluator.evaluate(length, stream, parent).longValue(), encoding);
            return new StringValue(parent, str);
        } else {
            String str = stream.readStrz(encoding, terminator, includeTerminator, consumeTerminator, false);
            return new StringValue(parent, str);
        }
    }

    @Override
    public String getName() {
        return isFixedLenth ? "str" : "strz";
    }

    @Override
    public String getDisplayName() {
        return "string";
    }
}
