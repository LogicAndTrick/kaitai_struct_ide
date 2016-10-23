package io.kaitai.struct.visualizer.ui;

import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.ReadResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

/**
 * A UI control to view the contents of a result hex viewer
 */
public class ResultViewer extends JPanel {
    private final RSyntaxTextArea textArea;
    private final RTextScrollPane scrollPane;
    private final JScrollBar scrollBar;
    private final Gutter gutter;
    private final JTable table;
    private final HexTableModel tableModel;

    private ReadResult result;
    private KaitaiStream stream;
    private KObject object;

    public ResultViewer() {
        super(new BorderLayout());

        textArea = new RSyntaxTextArea(25, 70);
        textArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        // Hide the default scroll bar
        scrollPane = new RTextScrollPane(textArea, false);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        // Set up custom line numbers
        gutter = scrollPane.getGutter();

        // Use a custom scroll bar
        scrollBar = new JScrollBar(JScrollBar.VERTICAL);

        // Hex table
        table = new JTable();
        table.setModel(tableModel = new HexTableModel());
        table.setDefaultRenderer(Object.class, new HexTableRenderer());

        add(scrollBar, BorderLayout.EAST);
        add(scrollPane, BorderLayout.CENTER);
        add(table, BorderLayout.SOUTH);

        // Attach reprint listeners
        addListeners();

        update(null, null, null);
    }

    private void addListeners() {
        // When the text area is resized, we might have more lines to be printed
        textArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reprint();
            }
        });

        // When the mouse wheel is scrolled, we need to adjust the scroll bar
        textArea.addMouseWheelListener(new MouseInputAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int value = scrollBar.getValue();
                value += 3 * e.getWheelRotation();
                if (value < 0) value = 0;
                scrollBar.setValue(value);
            }
        });

        // When the scroll bar is moved, we need to reprint the output
        scrollBar.addAdjustmentListener(e -> {
            updateGutter();
            reprint();
        });

        // When the caret changes, we need to update the table of values
        textArea.addCaretListener(e -> tableModel.fireTableDataChanged());
    }

    private void updateGutter() {
        int value = scrollBar.getValue();
        this.gutter.setLineNumberingStartIndex(value);
    }

    public void update(ReadResult result, KObject object, KaitaiStream stream) {
        this.result = result;
        this.object = object;
        this.stream = stream;

        if (stream != null) {
            try {
                scrollBar.setMinimum(0);
                scrollBar.setMaximum((int) (stream.size() / 16));
            } catch (IOException e) {
                //
            }
        }

        reprint();
    }

    private void reprint() {
        String text;

        if (stream == null) {
            text = "No file open";
        } else {

            int startPosition = scrollBar.getValue() * 16;
            int lines = (int) Math.ceil(scrollPane.getHeight() / (double) textArea.getLineHeight());

            KObject obj = object == null ? KObject.EMPTY : object;
            ReadResult res = result == null ? new ReadResult() : result;
            try {
                text = Printer.print(stream, obj, res, lines, startPosition, startPosition + 1000);
            } catch (IOException e) {
                text = e.toString();
            }
        }

        int mark = textArea.getCaret().getDot();
        textArea.setText(text);
        textArea.getCaret().setDot(mark);
    }

    private long getCaretPositionInStream() {
        int dot = textArea.getCaret().getDot();
        String[] lines = textArea.getText().split("\\n");
        String indexLine = null;
        for (String line : lines) {
            if (!line.startsWith(" ")) indexLine = line;
            int len = line.length() + 1;
            if (len < dot) {
                dot -= len;
            } else {
                // 0000:  00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 | [ ... ]
                if (dot < 7 || dot > 54) return -1;
                int pos = (dot - 7) / 3;
                if (indexLine != null) {
                    int offset = Integer.parseInt(indexLine.split(":")[0], 16) * 16;
                    return offset + pos;
                }
            }
        }
        return -1;
    }

    /*
         s2le  | u2le | s2be  | u2be
         s4le  | u4le | s4be  | u4be
         s8le  | u8le | s8be  | u8be
         f4le  | f8le | f4be  | f8be
         ASCII |      | UTF-8
    */
    private class HexTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return 5;
        }

        @Override
        public int getColumnCount() {
            return 8;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            try {
                if (columnIndex % 2 == 1) {
                    if (stream == null) return null;
                    long pos = getCaretPositionInStream();
                    if (pos < 0) return null;
                    stream.seek(pos);
                }
                switch (columnIndex) {
                    case 0:
                        switch (rowIndex) {
                            case 0: return "s2le";
                            case 1: return "s4le";
                            case 2: return "s8le";
                            case 3: return "f4le";
                            case 4: return "ASCII";
                        }
                    case 1:
                        switch (rowIndex) {
                            case 0: return stream.readS2le();
                            case 1: return stream.readS4le();
                            case 2: return stream.readS8le();
                            case 3: return stream.readF4le();
                            case 4: return stream.readStrz("ASCII", 0, false, true, true);
                        }
                    case 2:
                        switch (rowIndex) {
                            case 0: return "u2le";
                            case 1: return "u4le";
                            case 2: return "u8le";
                            case 3: return "f8le";
                            case 4: return "";
                        }
                    case 3:
                        switch (rowIndex) {
                            case 0: return stream.readU2le();
                            case 1: return stream.readU4le();
                            case 2: return stream.readU8le();
                            case 3: return stream.readF8le();
                            case 4: return "";
                        }
                    case 4:
                        switch (rowIndex) {
                            case 0: return "s2be";
                            case 1: return "s4be";
                            case 2: return "s8be";
                            case 3: return "f4be";
                            case 4: return "UTF-8";
                        }
                    case 5:
                        switch (rowIndex) {
                            case 0: return stream.readS2be();
                            case 1: return stream.readS4be();
                            case 2: return stream.readS8be();
                            case 3: return stream.readF4be();
                            case 4: return stream.readStrz("UTF-8", 0, false, true, true);
                        }
                    case 6:
                        switch (rowIndex) {
                            case 0: return "u2be";
                            case 1: return "u4be";
                            case 2: return "u8be";
                            case 3: return "f8be";
                            case 4: return "";
                        }
                    case 7:
                        switch (rowIndex) {
                            case 0: return stream.readU2be();
                            case 1: return stream.readU4be();
                            case 2: return stream.readU8be();
                            case 3: return stream.readF8be();
                            case 4: return "";
                        }
                }
            } catch (Exception ex) {
                //
            }

            return null;
        }
    }

    private class HexTableRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column % 2 == 1) {
                com.setForeground(Color.gray);
            } else {
                com.setForeground(Color.black);
            }

            return com;
        }
    }
}
