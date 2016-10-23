package io.kaitai.struct.visualizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.format.ClassSpec;
import io.kaitai.struct.runtime.KObject;
import io.kaitai.struct.runtime.results.ReadResult;
import io.kaitai.struct.visualizer.ui.ResultViewer;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Editor extends JFrame {
    private JPanel rootPanel;
    private JSplitPane splitPanel;
    private RSyntaxTextArea textEditor;
    private JLabel statusLabel;
    private ResultViewer viewer;

    public Editor() {
        super("Kaitai IDE");

        setContentPane(rootPanel);

        setMenu();
        addLeftPane();
        addRightPane();

        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


        textEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { }

            @Override
            public void removeUpdate(DocumentEvent e) { }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recompile();
            }
        });

        recompile();
    }

    private void addLeftPane() {

        JPanel left = new JPanel(new BorderLayout());

        JPanel fileOpen = new JPanel(new BorderLayout());
        JLabel fileLabel = new JLabel("No file open");
        JButton fileButton = new JButton("Browse");
        fileButton.addActionListener(e -> {
            FileDialog fd = new FileDialog(Editor.this, "Select KSY file");
            fd.setVisible(true);
            if (fd.getFile() != null) {
                Path path = FileSystems.getDefault().getPath(fd.getDirectory(), fd.getFile());
                if (Files.exists(path)) {
                    try {
                        String contents = new String(Files.readAllBytes(path), Charset.forName("UTF-8"));
                        textEditor.setText(contents);
                        fileLabel.setText(fd.getFile());
                        recompile();
                    } catch (IOException e1) {
                        statusLabel.setText(e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }
        });
        fileOpen.add(fileLabel, BorderLayout.CENTER);
        fileOpen.add(fileButton, BorderLayout.EAST);

        textEditor = new RSyntaxTextArea(25, 70);
        RTextScrollPane scrollPane = new RTextScrollPane(textEditor, true);

        statusLabel = new JLabel();
        statusLabel.setForeground(Color.red);

        left.add(fileOpen, BorderLayout.NORTH);
        left.add(scrollPane, BorderLayout.CENTER);
        left.add(statusLabel, BorderLayout.SOUTH);

        splitPanel.setLeftComponent(left);
    }

    private void addRightPane() {


        JPanel right = new JPanel(new BorderLayout());

        JPanel fileOpen = new JPanel(new BorderLayout());
        JLabel fileLabel = new JLabel("No file open");
        JButton fileButton = new JButton("Browse");
        fileButton.addActionListener(e -> {
            FileDialog fd = new FileDialog(Editor.this, "Select binary file");
            fd.setVisible(true);
            if (fd.getFile() != null) {
                Path path = FileSystems.getDefault().getPath(fd.getDirectory(), fd.getFile());
                if (Files.exists(path)) {
                    try {
                        currentStream = new KaitaiStream(path.toString());
                        fileLabel.setText(fd.getFile());
                        recompile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        fileOpen.add(fileLabel, BorderLayout.CENTER);
        fileOpen.add(fileButton, BorderLayout.EAST);

        right.add(fileOpen, BorderLayout.NORTH);

        viewer = new ResultViewer();
        right.add(viewer, BorderLayout.CENTER);

        splitPanel.setRightComponent(right);
    }

    private void setMenu() {
        MenuBar bar = new MenuBar();

        Menu file = new Menu("File");
        MenuItem open = new MenuItem("Open");
        file.add(open);

        bar.add(file);

        setMenuBar(bar);
    }

    private void recompile() {
        statusLabel.setText("");

        String text = textEditor.getText();
        if (text != null && !text.isEmpty()) {
            try {

                Reader reader = new StringReader(text);
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

                ClassSpec spec = mapper.readValue(reader, ClassSpec.class);

                String name = null;
                if (spec.meta().isDefined()) {
                    name = spec.meta().get().id();
                }
                if (name == null) name = "root";

                parsedObject = new KObject(spec, null, name);

            } catch (Exception e) {
                statusLabel.setText(e.getMessage());
                e.printStackTrace();
            }
        } else {
            this.parsedObject = null;
        }

        try {
            parseFile();
        } catch (Exception e) {
            statusLabel.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    private KObject parsedObject;
    private KaitaiStream currentStream;
    private ReadResult readResult;

    private void parseFile() throws IOException {

        if (currentStream != null && parsedObject != null) {

            currentStream.seek(0);
            readResult = new ReadResult();
            parsedObject.read(currentStream, readResult, null);
            readResult.evaluateLazyValues();
            readResult.index();

        } else {
            readResult = new ReadResult();
        }

        viewer.update(readResult, parsedObject, currentStream);
    }
}
