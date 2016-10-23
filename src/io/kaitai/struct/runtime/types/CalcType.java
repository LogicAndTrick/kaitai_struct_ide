package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;

/**
 * A type that is calculated from an expression instance.
 */
public class CalcType implements IType {

    public final static CalcType INSTANCE = new CalcType();

    @Override
    public Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return "calc";
    }

    @Override
    public String getDisplayName() {
        return "calculated type";
    }
}
