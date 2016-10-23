package io.kaitai.struct.runtime.results;

/**
 * A byte array (handled differently to an array value)
 */
public class BinaryValue extends PrimitiveValue {
    public BinaryValue(ContainerValue owner, byte[] value) {
        super(owner, value);
    }
}
