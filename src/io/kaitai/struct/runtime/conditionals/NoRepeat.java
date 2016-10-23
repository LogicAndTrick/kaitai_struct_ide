package io.kaitai.struct.runtime.conditionals;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.Value;

/**
 * Don't repeat: i.e. only iterate once.
 */
public class NoRepeat implements IRepeat {

    public final static NoRepeat INSTANCE  = new NoRepeat();

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean shouldStop(int iterationNum, KaitaiStream stream, Value lastValue, ContainerValue context) {
        return iterationNum > 0;
    }
}
