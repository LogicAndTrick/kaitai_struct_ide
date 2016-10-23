package io.kaitai.struct.runtime.results;

/**
 * A boolean value
 */
public class BoolValue extends PrimitiveValue {
    public BoolValue(ContainerValue owner, Boolean value) {
        super(owner, value);
    }
}
