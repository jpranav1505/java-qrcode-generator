import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.Paths;
import java.util.Scanner;

public class QRCodeGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Enter the text or URL to encode into the QR Code:");
            String data = scanner.nextLine();

            // The output path for the QR Code image
            String path = "./qrcode.png";

            // Create the BitMatrix (the QR code's data)
            BitMatrix matrix = new MultiFormatWriter().encode(
                data,
                BarcodeFormat.QR_CODE,
                500, // width
                500  // height
            );

            // Write the BitMatrix to an image file
            MatrixToImageWriter.writeToPath(matrix, "PNG", Paths.get(path));

            System.out.println("\nSuccessfully generated QR Code!");
            System.out.println("It is saved as 'qrcode.png' in the project folder.");

        } catch (Exception e) {
            System.err.println("Error generating QR Code: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}