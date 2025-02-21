package QRcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    /**
     * Génère un QR Code avec un fond transparent.
     * @param data Données à encoder dans le QR Code.
     * @param width Largeur du QR Code.
     * @param height Hauteur du QR Code.
     * @return Image JavaFX contenant le QR Code transparent.
     */
    public static Image generateQRCode(String data, int width, int height) {
        try {
            if (data == null || data.trim().isEmpty()) {
                data = "Aucune donnée disponible";
            }

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            // 🔹 Créer une image transparente
            BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // Si c'est un pixel noir, on le garde, sinon on met transparent
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : 0x00FFFFFF);
                }
            }

            // Convertir BufferedImage en Image JavaFX
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            return new Image(new ByteArrayInputStream(baos.toByteArray()));

        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
