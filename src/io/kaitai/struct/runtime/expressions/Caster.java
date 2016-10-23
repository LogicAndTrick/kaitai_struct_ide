package io.kaitai.struct.runtime.expressions;

import java.math.BigInteger;

/**
 * Utility methods for dealing with numbers and automatically handling type casting and so on.
 */
public class Caster {

    private enum NumType {
        Byte,
        Short,
        Int,
        Long,
        BigInt,
        Float,
        Double
    }

    /**
     * Find the biggest type classification in an array of numbers
     * @param numbers The list of number objects
     * @return The biggest type of the numbers in the array
     */
    private static NumType getType(Number... numbers) {
        NumType ty = NumType.Byte;
        for (Number n : numbers) {
            NumType nt = NumType.Int;
            if (n instanceof Byte) nt = NumType.Byte;
            else if (n instanceof Short) nt = NumType.Short;
            else if (n instanceof Integer) nt = NumType.Int;
            else if (n instanceof Long) nt = NumType.Long;
            else if (n instanceof BigInteger) nt = NumType.BigInt;
            else if (n instanceof Float) nt = NumType.Float;
            else if (n instanceof Double) nt = NumType.Double;
            if (nt.ordinal() > ty.ordinal()) ty = nt;
        }
        return ty;
    }

    private static BigInteger castBigInt(Number num) {
        if (num instanceof BigInteger) {
            return ((BigInteger) num);
        }
        return BigInteger.valueOf(num.longValue());
    }

    /**
     * Compares numbers
     */
    public static int compare(Number left, Number right) {
        NumType type = getType(left, right);
        switch (type) {
            case Byte: return Byte.compare(left.byteValue(), right.byteValue());
            case Short: return Short.compare(left.shortValue(), right.shortValue());
            case Int: return Integer.compare(left.intValue(), right.intValue());
            case Long: return Long.compare(left.longValue(), right.longValue());
            case BigInt: return castBigInt(left).compareTo(castBigInt(right));
            case Float: return Float.compare(left.floatValue(), right.floatValue());
            case Double: return Double.compare(left.doubleValue(), right.doubleValue());
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Adds numbers
     */
    public static Number add(Number left, Number right) {
        NumType type = getType(left, right);
        switch (type) {
            case Byte: return left.byteValue() + right.byteValue();
            case Short: return left.shortValue() + right.shortValue();
            case Int: return left.intValue() + right.intValue();
            case Long: return left.longValue() + right.longValue();
            case BigInt: return castBigInt(left).add(castBigInt(right));
            case Float: return left.floatValue() + right.floatValue();
            case Double: return left.doubleValue() + right.doubleValue();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Subtracts numbers
     */
    public static Number sub(Number left, Number right) {
        NumType type = getType(left, right);
        switch (type) {
            case Byte: return left.byteValue() - right.byteValue();
            case Short: return left.shortValue() - right.shortValue();
            case Int: return left.intValue() - right.intValue();
            case Long: return left.longValue() - right.longValue();
            case BigInt: return castBigInt(left).subtract(castBigInt(right));
            case Float: return left.floatValue() - right.floatValue();
            case Double: return left.doubleValue() - right.doubleValue();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Multiplies numbers
     */
    public static Number mult(Number left, Number right) {
        NumType type = getType(left, right);
        switch (type) {
            case Byte: return left.byteValue() * right.byteValue();
            case Short: return left.shortValue() * right.shortValue();
            case Int: return left.intValue() * right.intValue();
            case Long: return left.longValue() * right.longValue();
            case BigInt: return castBigInt(left).multiply(castBigInt(right));
            case Float: return left.floatValue() * right.floatValue();
            case Double: return left.doubleValue() * right.doubleValue();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Divides numbers
     */
    public static Number div(Number left, Number right) {
        NumType type = getType(left, right);
        switch (type) {
            case Byte: return left.byteValue() / right.byteValue();
            case Short: return left.shortValue() / right.shortValue();
            case Int: return left.intValue() / right.intValue();
            case Long: return left.longValue() / right.longValue();
            case BigInt: return castBigInt(left).divide(castBigInt(right));
            case Float: return left.floatValue() / right.floatValue();
            case Double: return left.doubleValue() / right.doubleValue();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Performs bitwise AND on integers
     */
    public static Number and(Number left, Number right) {
        NumType type = getType(left, right);
        switch (type) {
            case Byte: return left.byteValue() & right.byteValue();
            case Short: return left.shortValue() & right.shortValue();
            case Int: return left.intValue() & right.intValue();
            case Long: return left.longValue() & right.longValue();
            case BigInt: return castBigInt(left).and(castBigInt(right));
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Performs bitwise OR on integers
     */
    public static Number or(Number left, Number right) {
        NumType type = getType(left, right);
        switch (type) {
            case Byte: return left.byteValue() | right.byteValue();
            case Short: return left.shortValue() | right.shortValue();
            case Int: return left.intValue() | right.intValue();
            case Long: return left.longValue() | right.longValue();
            case BigInt: return castBigInt(left).or(castBigInt(right));
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Performs bitwise XOR on integers
     */
    public static Number xor(Number left, Number right) {
        NumType type = getType(left, right);
        switch (type) {
            case Byte: return left.byteValue() ^ right.byteValue();
            case Short: return left.shortValue() ^ right.shortValue();
            case Int: return left.intValue() ^ right.intValue();
            case Long: return left.longValue() ^ right.longValue();
            case BigInt: return castBigInt(left).xor(castBigInt(right));
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Performs bitwise left shift on integers
     */
    public static Number lshift(Number left, Number right) {
        NumType type = getType(left, right);
        switch (type) {
            case Byte: return left.byteValue() << right.byteValue();
            case Short: return left.shortValue() << right.shortValue();
            case Int: return left.intValue() << right.intValue();
            case Long: return left.longValue() << right.longValue();
            case BigInt: return castBigInt(left).shiftLeft(right.intValue());
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Performs bitwise right shift on integers
     */
    public static Number rshift(Number left, Number right) {
        NumType type = getType(left, right);
        switch (type) {
            case Byte: return left.byteValue() >> right.byteValue();
            case Short: return left.shortValue() >> right.shortValue();
            case Int: return left.intValue() >> right.intValue();
            case Long: return left.longValue() >> right.longValue();
            case BigInt: return castBigInt(left).shiftRight(right.intValue());
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Performs bitwise inverse on integers
     */
    public static Number invert(Number num) {
        NumType type = getType(num);
        switch (type) {
            case Byte: return ~num.byteValue();
            case Short: return ~num.shortValue();
            case Int: return ~num.intValue();
            case Long: return ~num.longValue();
            case BigInt: return ~num.longValue();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Multiplies a number by negative one
     */
    public static Number negate(Number num) {
        return mult(num, -1);
    }
}
