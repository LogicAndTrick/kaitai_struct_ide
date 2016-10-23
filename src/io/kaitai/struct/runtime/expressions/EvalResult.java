package io.kaitai.struct.runtime.expressions;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.results.*;

/**
 * The result of an evaluation
 */
public class EvalResult {
    private Value value;

    public EvalResult(Value value) {
        this.value = value;
    }

    public byte byteValue() {
        return ((Number) value.getValue()).byteValue();
    }

    public short shortValue() {
        return ((Number) value.getValue()).shortValue();
    }

    public int intValue() {
        return ((Number) value.getValue()).intValue();
    }

    public long longValue() {
        return ((Number) value.getValue()).longValue();
    }

    public float floatValue() {
        return ((Number) value.getValue()).floatValue();
    }

    public double doubleValue() {
        return ((Number) value.getValue()).doubleValue();
    }

    public boolean boolValue() {
        return (Boolean) value.getValue();
    }

    public Value getValue() {
        return value;
    }

    public KaitaiStream streamValue() {
        return (KaitaiStream) value.getValue();
    }
}
