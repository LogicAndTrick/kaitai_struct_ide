package io.kaitai.struct.visualizer.ui;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Prints a ReadResult to a string in a nice hex viewer style format.
 */
public class Printer {
    public static String print(KaitaiStream stream, KObject object, ReadResult result, int numLines, long startPosition, long endPosition) throws IOException {
        stream.seek(startPosition);

        StringPrinter sb = new StringPrinter(numLines);

        while (!stream.isEof() && stream.pos() < endPosition && !sb.isFull()) {
            printChunk(sb, stream, object, result, startPosition, endPosition);
        }
        return sb.toString();
    }

    private static void printChunk(StringPrinter sb, KaitaiStream stream, KObject object, ReadResult result, long startPosition, long endPosition) throws IOException {
        long index = stream.pos();

        ArrayList<Value> nextValues = result.getNextValues(index);
        for (Value rv : nextValues) {

            // Print the bytes before the value
            printRaw(sb, stream, rv.getStartIndex() - stream.pos());

            if (rv.isArray()) {
                //printArrayValue(sb, rv, stream, object, result);
            } else if (rv.isPrimitive()) {
                printPrimitiveValue(sb, rv, stream, object, result, startPosition, endPosition);
            } else if (rv.isContainer()) {
                //printContainerValue(sb, rv, stream, object, result);
            }
        }

        // No values found - print a null
        if (stream.pos() == index) {
            printPrimitiveValue(sb, null, stream, object, result, startPosition, endPosition);
        }
    }

    private static void printPrimitiveValue(StringPrinter sb, Value val, KaitaiStream stream, KObject object, ReadResult result, long startPosition, long endPosition) throws IOException {
        long index = stream.pos();

        if (val == null) {
            // Find the next value and print the bytes between now and then
            Value next = result.getNextPrimitiveValue(index + 1);
            long end = next == null ? stream.size() : next.getStartIndex();
            if (end > endPosition) end = endPosition;
            printRaw(sb, stream, end - index);
        } else if (val.getStartIndex() == val.getEndIndex()) {

            // If this value is null, just skip it because we don't care
            if (val.getValue() == null) {
                return;
            }

            // Empty row - just print the empty variable
            printHexLine(sb, 0, new byte[0]);

            String rs = toReadableString(val.getValue());
            String dn = val.getDisplayName() + ":";
            //while (dn.length() < 25) dn += ' ';

            sb.append("| ").append(dn).append(' ').append(rs);
            sb.appendLine();
        } else {
            // Print the bytes in the value
            printValue(sb, stream, val);
        }
    }

    private static void printArrayValue(StringPrinter sb, Value value, KaitaiStream stream, KObject object, ReadResult result) throws IOException {
        printHexLine(sb, 0, new byte[0]);
        sb.append("| >> ");
        sb.append(value.getName());
        sb.append("[] (");
        sb.append(value.getType().getName());
        sb.append("[])");
        sb.appendLine();
    }

    private static void printContainerValue(StringPrinter sb, Value value, KaitaiStream stream, KObject object, ReadResult result) throws IOException {
        printHexLine(sb, 0, new byte[0]);
        sb.append("| --- ");
        sb.append(value.getName());
        if (value.getArrayIndex() >= 0) {
            sb.append('[').append(value.getArrayIndex()).append(']');
        }
        sb.append(" --- (");
        sb.append(value.getType().getName());
        sb.append(")");
        sb.appendLine();
    }

    private static void printValue(StringPrinter sb, KaitaiStream stream, Value value) throws IOException {
        printBytes(sb, stream, value.getEndIndex() - value.getStartIndex(), (line, idx, data) -> {
            if (line == 0) {
                String rs = toReadableString(value.getValue());
                String dn = value.getDisplayName() + ":";
                //while (dn.length() < 25) dn += ' ';
                return dn + " " + rs;
            } else {
                return ".";
            }
        });
    }

    private static void printRaw(StringPrinter sb, KaitaiStream stream, long count) throws IOException {
        if (count == 0) return;
        printBytes(sb, stream, count, (line, idx, data) -> "[ " + toReadableByteString(idx, data) + " ]");
    }

    @FunctionalInterface
    interface LineCallback<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    private static void printBytes(StringPrinter sb, KaitaiStream stream, long count, LineCallback<Integer, Long, byte[], String> perLine) throws IOException {
        long index = stream.pos();
        long end = index + count;
        long i = index;
        int currentLine = 0;
        while (i < end) {
            long line = 16 - (i % 16);
            if (i + line > end) {
                line = end - i;
            }
            byte[] data = stream.readBytes(line);
            printHexLine(sb, i, data);
            sb.append('|').append(' ');
            sb.append(perLine.apply(currentLine++, i, data));
            sb.appendLine();
            i += line;
        }
    }

    private static void printHexLine(StringPrinter sb, long startIndex, byte[] bytes) {

        long start = startIndex % 16;

        // print line number if this is the first byte of the line
        if (start == 0) {
            long num = startIndex / 16;
            String line = String.format("%04X:  ", num);
            sb.append(line);
        } else {
            sb.append("       ");
        }

        int i = 0;
        for (; i < start; i++) {
            sb.append("   ");
        }
        for (int j = 0; j < bytes.length; j++, i++) {
            sb.append(getHexCode(bytes[j])).append(' ');
        }
        for (; i < 16; i++) {
            sb.append("   ");
        }
    }

    private static String toReadableByteString(Long index, byte[] bytes) {
        index = index % 16;
        char[] ret = new char[16];
        int i = 0;
        for (; i < index; i++) {
            ret[i] = ' ';
        }
        for (int j = 0; j < bytes.length; j++, i++) {
            byte c = bytes[j];
            ret[i] = c >= 0x21 && c <= 0x7E ? (char) c : '.';
        }
        for (; i < 16; i++) {
            ret[i] = ' ';
        }
        return new String(ret);
    }

    private static String toReadableString(Object obj) {
        if (obj == null) {
            return "<null>";
        }
        if (obj instanceof byte[]) {
            byte[] bytes = (byte[]) obj;
            if (bytes.length > 16) {
                return "{ " + bytes.length + " bytes }";
            }
            char[] ret = new char[bytes.length];
            for (int j = 0; j < bytes.length; j++) {
                byte c = bytes[j];
                ret[j] = c >= 0x21 && c <= 0x7E ? (char) c : '.';
            }
            return new String(ret);
        }
        if (obj instanceof Object[]) {
            Object[] array = (Object[]) obj;
            StringBuilder sb = new StringBuilder("[ ");
            for (Object o : array) {
                sb.append(toReadableString(o));
            }
            sb. append(" ]");
            return sb.toString();
        }
        return obj.toString();
    }

    private static final char[] HEXADECIMAL = "0123456789ABCDEF".toCharArray();

    private static String getHexCode(byte b) {
        return new String(new char[]{
                HEXADECIMAL[(b >> 4) & 0xF],
                HEXADECIMAL[(b & 0xF)]
        });
    }
}
