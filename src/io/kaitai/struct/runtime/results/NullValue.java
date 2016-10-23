package io.kaitai.struct.runtime.results;

/**
 * A value of null
 */
public class NullValue extends PrimitiveValue {
    public NullValue(ContainerValue owner) {
        super(owner, null);
    }
}
