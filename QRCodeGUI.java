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
    JLabel qrCodeLabel;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem saveMenuItem;

    // --- QR Code Data ---
    private BufferedImage currentQRCodeImage;

    public QRCodeGUI() {
        // --- Define our new color palette (NEW) ---
        Color bgColor = Color.decode("#2B2B2B"); // Dark gray background
        Color fieldColor = Color.decode("#3C3F41"); // Lighter gray for fields
        Color textColor = Color.decode("#BBBBBB"); // Light gray for text
        Color accentColor = Color.decode("#4A85C1"); // A nice blue for the button

        // --- Main Frame ---
        frame = new JFrame("Colorful QR Code Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 500);
        frame.setLayout(null);
        frame.getContentPane().setBackground(bgColor); // COLOR: Set frame background

        // --- Menu Bar ---
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        saveMenuItem = new JMenuItem("Save As...");
        saveMenuItem.addActionListener(this);
        // COLOR: Style the menu
        menuBar.setBackground(fieldColor);
        fileMenu.setForeground(textColor);
        saveMenuItem.setBackground(fieldColor);
        saveMenuItem.setForeground(textColor);
        fileMenu.add(saveMenuItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        // --- Text Field for Input ---
        textField = new JTextField();
        textField.setBounds(50, 20, 330, 30);
        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateQRCode();
            }
        });
        // COLOR: Style the text field
        textField.setBackground(fieldColor);
        textField.setForeground(textColor);
        textField.setCaretColor(Color.WHITE); // Sets the blinking cursor color
        textField.setBorder(BorderFactory.createLineBorder(accentColor));

        // --- Clear Button ---
        clearButton = new JButton("Clear");
        clearButton.setBounds(160, 60, 100, 30);
        clearButton.addActionListener(this);
        // COLOR: Style the button
        clearButton.setBackground(accentColor);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusable(false);
        clearButton.setBorderPainted(false);

        // --- QR Code Preview Label ---
        qrCodeLabel = new JLabel();
        qrCodeLabel.setBounds(65, 110, 300, 300);
        qrCodeLabel.setBorder(BorderFactory.createLineBorder(fieldColor));

        // --- Add Components to Frame ---
        frame.add(textField);
        frame.add(clearButton);
        frame.add(qrCodeLabel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new QRCodeGUI();
    }
    
    private void updateQRCode() {
        String data = textField.getText();
        if (data.trim().isEmpty()) {
            qrCodeLabel.setIcon(null);
            currentQRCodeImage = null;
            return;
        }
        
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 300, 300);
            currentQRCodeImage = MatrixToImageWriter.toBufferedImage(matrix);
            qrCodeLabel.setIcon(new ImageIcon(currentQRCodeImage));
        } catch (Exception ex) {
            qrCodeLabel.setIcon(null);
            currentQRCodeImage = null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearButton) {
            textField.setText("");
            qrCodeLabel.setIcon(null);
            currentQRCodeImage = null;
        }
        
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