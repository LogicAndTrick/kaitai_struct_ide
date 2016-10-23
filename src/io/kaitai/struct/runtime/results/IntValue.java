package io.kaitai.struct.runtime.results;

/**
 * An integer number
 */
public class IntValue extends NumericValue {
    public IntValue(ContainerValue owner, Number value) {
        super(owner, value);
    }
}
