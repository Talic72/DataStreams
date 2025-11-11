import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Runner extends JFrame {

    private JTextArea txtOriginal;
    private JTextArea txtFiltered;
    private JTextField txtSearch;
    private JButton btnLoad;
    private JButton btnSearch;
    private JButton btnQuit;
    private File selectedFile;

    public Runner() {
        super("Data Stream Processing");

        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
//top panel
        JPanel topPanel = new JPanel();
        JLabel lblSearch = new JLabel("Search String:");
        txtSearch = new JTextField(20);
        topPanel.add(lblSearch);
        topPanel.add(txtSearch);
        add(topPanel, BorderLayout.NORTH);

        //center panel
        txtOriginal = new JTextArea();
        txtFiltered = new JTextArea();
        txtOriginal.setEditable(false);
        txtFiltered.setEditable(false);

        JScrollPane scrollLeft = new JScrollPane(txtOriginal);
        JScrollPane scrollRight = new JScrollPane(txtFiltered);
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.add(scrollLeft);
        centerPanel.add(scrollRight);
        add(centerPanel, BorderLayout.CENTER);
//btns
        JPanel bottomPanel = new JPanel();
        btnLoad = new JButton("Load File");
        btnSearch = new JButton("Search");
        btnQuit = new JButton("Quit");
        bottomPanel.add(btnLoad);
        bottomPanel.add(btnSearch);
        bottomPanel.add(btnQuit);
        add(bottomPanel, BorderLayout.SOUTH);
        btnLoad.addActionListener(this::loadFile);
        btnSearch.addActionListener(this::searchFile);
        btnQuit.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void loadFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int choice = chooser.showOpenDialog(this);

        if (choice == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();

            try (Stream<String> lines = Files.lines(selectedFile.toPath())) {
                String allText = lines.collect(Collectors.joining("\n"));
                txtOriginal.setText(allText);
                txtFiltered.setText("");
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
        }
    }
    private void searchFile(ActionEvent e) {
        if (selectedFile == null)
        {
            JOptionPane.showMessageDialog(this, "Please load a file first.");
            return;
        }

        String search = txtSearch.getText().trim();
        if (search.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter a search string.");
            return;
        }

        try (Stream<String> lines = Files.lines(selectedFile.toPath())) {
            List<String> matches = lines
                    .filter(line -> line.toLowerCase().contains(search.toLowerCase())) //stream + Lambda use
                    .collect(Collectors.toList());
            txtFiltered.setText(String.join("\n", matches));
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Runner());
    }
}
