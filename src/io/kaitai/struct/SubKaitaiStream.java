package io.kaitai.struct;

import java.io.IOException;

public class SubKaitaiStream extends KaitaiStream {

    public SubKaitaiStream(KaitaiStream stream, long start, long end) {
        this.st = new KaitaiStreamSlice(stream.st, start, end);
    }

    protected class KaitaiStreamSlice implements KaitaiSeekableStream {
        private KaitaiSeekableStream stream;
        private long start;
        private long end;

        public KaitaiStreamSlice(KaitaiSeekableStream stream, long start, long end) {
            this.stream = stream;
            this.start = start;
            this.end = end;
        }

        @Override
        public void close() throws IOException {
            // Don't close the parent
        }

        @Override
        public long pos() throws IOException {
            return stream.pos();
        }

        @Override
        public long size() throws IOException {
            return end - start;
        }

        @Override
        public void seek(long l) throws IOException {
            stream.seek(start + l);
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public int read(byte[] buf) throws IOException {
            return stream.read(buf);
        }

        @Override
        public boolean isEof() throws IOException {
            return stream.pos() >= end;
        }
    }

}
