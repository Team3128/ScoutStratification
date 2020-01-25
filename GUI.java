
/**
 * @author Mitchell Shapiro
 * January 2020
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI extends JPanel implements ActionListener, MouseListener {

    private static final long serialVersionUID = 1L;
    private JFrame frame;

    private JTextArea text;
    private JScrollPane scroll;
    private JButton browse;
    private JLabel selected;
    private JPanel csvPanel;
    private FlowLayout csvLayout;

    private JPanel optionPanel;
    private JPanel topPanel;
    private FlowLayout topLayout;
    private BoxLayout optionBox;

    private JLabel scoutLabel;
    private JLabel maxPassLabel;
    private JLabel breakLengthLabel;
    private JTextField scoutField;
    private JTextField maxPassField;
    private JTextField breakLengthField;

    private FlowLayout bottomLayout;
    private JPanel bottomPanel;
    private JButton assign;
    private JButton abort;
    private JButton clearCsv;

    private JFileChooser fileChooser;

    private Sorter sorter;

    public GUI() {
        sorter = new Sorter();// Initialize the Sorter
        makeFrame();
    }

    // Make the GUI
    private void makeFrame() {
        frame = new JFrame(Constants.APPLICATION_NAME);// Make the frame, and set the title
        frame.setSize(Constants.SCREEN_SIZE);// Set the window size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Make the X button close the program
        frame.setResizable(false);// Make the window not resizeable
        frame.setLocationRelativeTo(null);// Make the window open in the center of the screen

        // Set the icon image of the window
        ImageIcon icon = new ImageIcon("logo.png");
        frame.setIconImage(icon.getImage());

        text = new JTextArea(9, 47);
        text.setBackground(new Color(208, 215, 217));// Set the background to a blueish grey

        Font textFont = new Font("Arial", text.getFont().getStyle(), text.getFont().getSize());
        text.setFont(textFont);
        text.setLineWrap(true);// Enable line wrap to make things cleaner

        setTextHint(true);// Show the hint as the text area won't be selected when the window opens

        text.addMouseListener(new MouseAdapter() {
            // Runs when the mouse clicks inside the text area
            @Override
            public void mouseClicked(MouseEvent e) {
                setTextHint(false);// Hide the hint
            }
        });
        // Check the assign button every time the user affects the text area
        text.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                setAssign();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setAssign();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setAssign();
            }
        });

        // The rest of this is just making a bunch of panels and filling them with the
        // elements

        optionPanel = new JPanel();
        optionBox = new BoxLayout(optionPanel, BoxLayout.Y_AXIS);
        optionPanel.setLayout(optionBox);

        topPanel = new JPanel();
        topLayout = new FlowLayout();
        topPanel.setLayout(topLayout);

        scoutLabel = new JLabel("Scouts:");
        scoutLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionPanel.add(scoutLabel);

        scoutField = new JTextField();
        scoutField.setText(Constants.DEFAULT_SCOUTS + "");
        optionPanel.add(scoutField);
        scoutField.addMouseListener(this);

        maxPassLabel = new JLabel("Max Passes:");
        maxPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionPanel.add(maxPassLabel);

        maxPassField = new JTextField();
        maxPassField.setText(Constants.DEFAULT_MAX_PASSES + "");
        optionPanel.add(maxPassField);
        maxPassField.addMouseListener(this);

        breakLengthLabel = new JLabel("Break Length:");
        breakLengthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionPanel.add(breakLengthLabel);

        breakLengthField = new JTextField();
        breakLengthField.setText(Constants.DEFAULT_BREAK_LENGTH + "");
        optionPanel.add(breakLengthField);
        breakLengthField.addMouseListener(this);

        optionPanel.setBorder(BorderFactory.createTitledBorder("Options"));

        csvPanel = new JPanel();
        csvLayout = new FlowLayout(FlowLayout.LEFT);
        csvPanel.setLayout(csvLayout);

        browse = new JButton("Select CSV");
        browse.addActionListener(this);

        selected = new JLabel(Constants.NONE_SELECTED_TEXT);

        csvPanel.add(browse);
        csvPanel.add(selected);
        scroll = new JScrollPane(text);

        topPanel.add(scroll);
        topPanel.add(optionPanel);

        bottomPanel = new JPanel();
        bottomLayout = new FlowLayout();
        bottomPanel.setLayout(bottomLayout);

        assign = new JButton("Assign Scouts");
        assign.setEnabled(false);
        assign.addActionListener(this);

        clearCsv = new JButton("Clear CSV");
        clearCsv.setEnabled(false);
        clearCsv.addActionListener(this);

        abort = new JButton("Abort");
        abort.setEnabled(false);
        abort.addActionListener(this);

        bottomPanel.add(assign);
        bottomPanel.add(clearCsv);
        bottomPanel.add(abort);

        BoxLayout mainBox = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(mainBox);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        csvPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel inputLabel = new JLabel("  Enter team data separated by single spaces or select a CSV file");

        this.add(topPanel);
        this.add(csvPanel);
        this.add(inputLabel);
        this.add(bottomPanel);

        frame.add(this);

        frame.setBackground(Color.WHITE);

        // Make the window visible, needs to be last
        frame.pack();
        frame.setVisible(true);

        // BEGIN CONSOLE CODE
        JFrame console = new JFrame("Console Output");
        console.setIconImage(icon.getImage());

        JTextArea ta = new JTextArea();
        JScrollPane consoleScroll = new JScrollPane(ta);
        ta.setLineWrap(true);
        ta.setEditable(false);

        TextAreaOutputStream taos = new TextAreaOutputStream(ta, 1000);
        PrintStream ps = new PrintStream(taos);
        System.setOut(ps);
        System.setErr(ps);

        console.add(consoleScroll);

        console.pack();
        console.setVisible(true);
        console.setSize(800, 600);
        // END CONSOLE CODE

        // Set the assign button as selected when the window opens
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                assign.requestFocus();
            }
        });
    }

    // Enable or disable the text hint in the text area
    // Enabled when it does not have focus, disabled when it does
    // Enable is whether to try to enable or disable the text hint
    private void setTextHint(boolean enable) {
        String hint = Constants.TEXT_HINT;

        // If trying to enable
        if (enable) {
            // If the text area is blank, set the hint
            if (text.getText().equals("")) {
                text.setText(hint);
                text.setForeground(Color.GRAY);
            }
            // Else if trying to disable
        } else {
            // If the hint is the same as the hint, clear it
            if (text.getText().equals(hint)) {
                text.setText("");
                text.setForeground(Color.BLACK);
            }
        }
        setAssign();// Check what the assign button status should be
    }

    // Start the optimization
    private void startMatches(String matches) {
        // Create a new thread so that the GUI does not hang while the optimization is
        // running
        Thread t1 = new Thread(new Runnable() {
            // Called when the thread starts
            @Override
            public void run() {
                int numScouts = Constants.DEFAULT_SCOUTS;
                int numMaxPasses = Constants.DEFAULT_MAX_PASSES;
                int breakLength = Constants.DEFAULT_BREAK_LENGTH;
                // Check all user input to ensure it is a valid int in bounds, and uses the
                // default values if they aren't
                try {
                    int tempScouts = Integer.valueOf(scoutField.getText());
                    if (tempScouts < 6) {
                        System.out.println(
                                "Number of scouts field must be >= 6, using the default: " + Constants.DEFAULT_SCOUTS);
                    } else {
                        numScouts = tempScouts;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid character in number of scouts field, using the default: "
                            + Constants.DEFAULT_SCOUTS);
                }
                try {
                    int tempPasses = Integer.valueOf(maxPassField.getText());
                    if (tempPasses < 1) {
                        System.out.println("Number of max passes field must be >= 1, using the default: "
                                + Constants.DEFAULT_MAX_PASSES);
                    } else {
                        numMaxPasses = tempPasses;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid character in number of max passes field, using the default: "
                            + Constants.DEFAULT_MAX_PASSES);
                }
                try {
                    int tempBreakLength = Integer.valueOf(breakLengthField.getText());
                    if (tempBreakLength < 1) {
                        System.out.println("Break length field must be >= 1, using the default: "
                                + Constants.DEFAULT_BREAK_LENGTH);
                    } else {
                        breakLength = tempBreakLength;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid character in break length field, using the default: "
                            + Constants.DEFAULT_BREAK_LENGTH);
                }
                // Start the sorter, this locks the thread until the optimization completes
                sorter.run(matches, numScouts, numMaxPasses, breakLength);

                abort.setEnabled(false);// Disable the abort button as the sorter is no longer running
                setAssign();// Reenable the assign button
            }
        });
        t1.start();// Start the thread
        abort.setEnabled(true);// Enable the abort button
        assign.setEnabled(false);// Disable the assign button
    }

    // Enable or disable the assign button
    private void setAssign() {
        if (text == null || selected == null)
            return;
        if ((!text.getText().equals(Constants.TEXT_HINT) && text.getText().length() > 0)
                || !selected.getText().equals(Constants.NONE_SELECTED_TEXT)) {
            if (sorter == null)
                return;
            if (sorter.isRunning()) {
                assign.setEnabled(false);
            } else {
                assign.setEnabled(true);
            }
        } else {
            assign.setEnabled(false);
        }
    }

    // Enable or disable the clear csv button
    private void setClearCsv() {
        if (selected == null)
            return;
        if (!selected.getText().equals(Constants.NONE_SELECTED_TEXT)) {
            clearCsv.setEnabled(true);
        } else {
            clearCsv.setEnabled(false);
        }
    }

    // Called when an action occurs, in this case when the buttons are pressed
    @Override
    public void actionPerformed(ActionEvent e) {
        setTextHint(true);// Show the text hint as the user clicked somewhere else
        // Check if this action is from the assign button
        if (e.getSource() == assign) {
            String selectedText = selected.getText();// Get the value of the csv file name
            // Check if the csv file name has been set to a csv file
            if (!selectedText.equals(Constants.NONE_SELECTED_TEXT)) {
                // Make sure the user selected a valid csv file
                if (selectedText.substring(selectedText.length() - 4).equals(".csv")) {
                    try {
                        // Read each line of the csv file
                        BufferedReader csvReader = new BufferedReader(
                                new FileReader(fileChooser.getSelectedFile().getAbsolutePath()));
                        String row;
                        String matches = "";
                        // Loop until the end of the file is reached
                        while ((row = csvReader.readLine()) != null) {
                            // csv files use commas to separate boxes, so we break each row up by commas to
                            // get the columns
                            String[] data = row.split(",");
                            // Each s is a single box
                            for (String s : data) {
                                // Disregard if the box is not a team number
                                if (isTeam(s)) {
                                    matches += s + " ";// Append to the match string with this team number
                                }
                            }
                        }
                        startMatches(matches);// Start the scout stratification
                        csvReader.close();// Close the reader to avoid a resource leak
                    } catch (IOException err) {
                        System.out.println(
                                "Error reading csv file. DEFAULT EXCEL FILES WON'T WORK! Export from a Google Sheet to csv, or save Excel as CSV (Comma delimited)");
                        err.printStackTrace();
                    }
                } else {
                    System.out.println("The selected file is not a valid csv file");
                }
                // If not, we read from the text area
            } else {
                String matches = text.getText();
                String[] split = matches.split(" ");// Split the String by spaces
                boolean good = true;// Boolean to verify if the match string is valid
                // Loop through each value
                for (String s : split) {
                    try {
                        Integer.valueOf(s);
                    } catch (NumberFormatException err) {
                        good = false;// Not good if any value is not a team number
                    }
                }
                if (good) {
                    // If good, start the assignment
                    startMatches(matches);
                } else {
                    System.out.println("INVALID MATCH STRING");
                }
            }
            // Check if this is the browse button
        } else if (e.getSource() == browse) {
            // This file chooser is uglier than some methods but easier to implement and set
            // file extensions
            JFileChooser jfc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");// Set a filter to only
                                                                                             // allow csv files
            jfc.setFileFilter(filter);
            fileChooser = jfc;

            int returnVal = jfc.showOpenDialog(null);// The value returned by the file chooser
            if (returnVal == JFileChooser.APPROVE_OPTION) {// If the file was approved, set the label to display the csv
                                                           // name
                System.out.println("Selected csv file: " + jfc.getSelectedFile().getAbsolutePath());
                selected.setText(jfc.getSelectedFile().getName());
            }
            setAssign();
            setClearCsv();
        } else if (e.getSource() == clearCsv) {
            selected.setText(Constants.NONE_SELECTED_TEXT);
            setAssign();
            setClearCsv();
            System.out.println("Cleared CSV file");
        } else if (e.getSource() == abort) {
            System.out.println("Stopping...");
            sorter.abort();
        }
    }

    // Return true if the given String is a valid team number (integer)
    private boolean isTeam(String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Detect when one of the option text fields are clicked so we show the text
    // hint
    @Override
    public void mouseClicked(MouseEvent e) {
        setTextHint(true);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public static void main(String[] args) {
        new GUI();
    }
}