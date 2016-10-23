package io.kaitai.struct.visualizer.ui;

/**
 * A basic string builder with a line count limit
 */
public class StringPrinter {
    private StringBuilder sb;
    private int currentLine;
    private int lines;

    public StringPrinter(int lines) {
        this.sb = new StringBuilder();
        this.lines = lines;
        this.currentLine = 0;
    }

    public StringPrinter append(String str, Object... args) {
        if (isFull()) return this;

        sb.append(String.format(str, args));
        return this;
    }

    public StringPrinter append(Object obj) {
        if (isFull()) return this;

        sb.append(obj);
        return this;
    }

    public StringPrinter appendLine() {
        if (isFull()) return this;

        sb.append('\n');
        currentLine++;
        return this;
    }

    public boolean isFull() {
        return this.currentLine > lines;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
