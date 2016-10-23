package io.kaitai.struct.runtime.conditionals;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.Value;

/**
 * Interface for the different styles of repeated data.
 */
public interface IRepeat {
    /**
     * Returns true if the result is an array.
     * @return true if the result is an array, false otherwise
     */
    boolean isArray();

    /**
     * Returns true when looping should be stopped.
     * @param iterationNum The current loop iteration number
     * @param stream The kaitai stream
     * @param lastValue The result of the previous iteration (null if this is the first iteration)
     * @param context The owner of the current context
     * @return true if iteration should stop
     */
    boolean shouldStop(int iterationNum, KaitaiStream stream, Value lastValue, ContainerValue context);
}
