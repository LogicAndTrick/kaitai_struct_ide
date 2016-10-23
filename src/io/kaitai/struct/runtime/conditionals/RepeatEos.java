package io.kaitai.struct.runtime.conditionals;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;

/**
 * Repeat until the end of the stream.
 */
public class RepeatEos implements IRepeat {

    public final static RepeatEos INSTANCE = new RepeatEos();

    public boolean isArray() {
        return true;
    }

    @Override
    public boolean shouldStop(int iterationNum, KaitaiStream stream, Value lastValue, ContainerValue context) {
        try {
            return stream.isEof();
        } catch (IOException e) {
            return true;
        }
    }
}
