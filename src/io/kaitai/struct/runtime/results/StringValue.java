package io.kaitai.struct.runtime.results;

/**
 * A string value
 */
public class StringValue extends PrimitiveValue {
    public StringValue(ContainerValue owner, String value) {
        super(owner, value);
    }

    private String str() {
        return (String) value;
    }

    @Override
    public Value getValue(String name) {
        if (name.equals("length")) {
            return new IntValue(owner, str().length());
        }
        if (name.equals("to_i")) {
            Number n = Long.parseLong(str());
            return new IntValue(owner, n);
        }
        return super.getValue(name);
    }
}
