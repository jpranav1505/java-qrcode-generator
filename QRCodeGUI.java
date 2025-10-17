import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

// We need these imports from the ZXing library
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class QRCodeGUI implements ActionListener {

    // --- GUI Components ---
    JFrame frame;
    JTextField textField;
    JButton clearButton;
    // NEW: A label to hold the QR code image for the preview
    JLabel qrCodeLabel;
    // NEW: A menu bar for file operations
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem saveMenuItem;

    // --- QR Code Data ---
    private BufferedImage currentQRCodeImage; // Holds the generated image in memory

    public QRCodeGUI() {
        // --- Main Frame ---
        frame = new JFrame("Enhanced QR Code Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 500); // Made it taller for the preview
        frame.setLayout(null);

        // --- Menu Bar (NEW) ---
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        saveMenuItem = new JMenuItem("Save As...");
        saveMenuItem.addActionListener(this);
        fileMenu.add(saveMenuItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        // --- Text Field for Input ---
        textField = new JTextField();
        textField.setBounds(50, 20, 330, 30);
        // NEW: Add a KeyListener to update the QR code as the user types
        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateQRCode();
            }
        });

        // --- Clear Button (NEW) ---
        clearButton = new JButton("Clear");
        clearButton.setBounds(160, 60, 100, 30);
        clearButton.addActionListener(this);

        // --- QR Code Preview Label (NEW) ---
        qrCodeLabel = new JLabel();
        qrCodeLabel.setBounds(65, 110, 300, 300); // Positioned below the text field
        qrCodeLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // --- Add Components to Frame ---
        frame.add(textField);
        frame.add(clearButton);
        frame.add(qrCodeLabel);
        frame.setVisible(true);
    }

    // Main method to start the application
    public static void main(String[] args) {
        new QRCodeGUI();
    }
    
    // This is our new helper method to generate and display the QR code
    private void updateQRCode() {
        String data = textField.getText();
        if (data.trim().isEmpty()) {
            qrCodeLabel.setIcon(null); // Clear the preview if text field is empty
            currentQRCodeImage = null;
            return;
        }
        
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 300, 300);
            currentQRCodeImage = MatrixToImageWriter.toBufferedImage(matrix);
            // Display the image in the JLabel
            qrCodeLabel.setIcon(new ImageIcon(currentQRCodeImage));
        } catch (Exception ex) {
            // Don't pop up an error for every typo, just clear the image
            qrCodeLabel.setIcon(null);
            currentQRCodeImage = null;
        }
    }

    // This method handles button and menu clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle "Clear" button click
        if (e.getSource() == clearButton) {
            textField.setText("");
            qrCodeLabel.setIcon(null);
            currentQRCodeImage = null;
        }
        
        // Handle "Save As..." menu item click
        if (e.getSource() == saveMenuItem) {
            if (currentQRCodeImage == null) {
                JOptionPane.showMessageDialog(frame, "There is no QR Code to save.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            int userSelection = fileChooser.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try {
                    // Ensure the file has a .png extension
                    String filePath = fileToSave.getAbsolutePath();
                    if (!filePath.toLowerCase().endsWith(".png")) {
                        fileToSave = new File(filePath + ".png");
                    }
                    ImageIO.write(currentQRCodeImage, "PNG", fileToSave);
                    JOptionPane.showMessageDialog(frame, "QR Code saved successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}