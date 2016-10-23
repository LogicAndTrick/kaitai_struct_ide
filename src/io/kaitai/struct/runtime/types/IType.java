package io.kaitai.struct.runtime.types;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.ContainerValue;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.runtime.results.Value;

import java.io.IOException;

/**
 * The interface for a type. A type is read from a stream and returns a value.
 */
public interface IType {

    /**
     * Read an instance of this type from the stream and return the value.
     * @param object The object to read
     * @param stream The kaitai stream object
     * @param read The result to read into
     * @param parent The owner of the current context
     * @return The value that is read from the stream
     * @throws IOException If the read fails
     */
    Value read(KObject object, KaitaiStream stream, ReadResult read, ContainerValue parent) throws IOException;

    /**
     * Get the name of this type
     */
    String getName();

    /**
     * Get the display name of this type
     */
    String getDisplayName();

    /**
     * Create a substream for this type
     */
    default KaitaiStream createStream(KaitaiStream stream, ContainerValue context) throws IOException {
        return stream;
    }
}
