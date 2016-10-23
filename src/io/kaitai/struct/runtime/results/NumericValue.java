package io.kaitai.struct.runtime.results;

import java.math.BigDecimal;

/**
 * Generic class for a numeric value
 */
public abstract class NumericValue extends PrimitiveValue {
    public NumericValue(ContainerValue owner, Number value) {
        super(owner, value);
    }

    public static NumericValue create(ContainerValue owner, Number value) {
        if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
            return new FloatValue(owner, value);
        } else {
            return new IntValue(owner, value);
        }
    }
}
