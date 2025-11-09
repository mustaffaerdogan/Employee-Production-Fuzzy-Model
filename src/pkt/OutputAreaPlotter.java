package pkt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Basit görselleştirici: eksen ve crisp değeri (dikey çizgi) ile bir PNG üretir.
 * Not: jFuzzyLogic’in iç kural/aggregasyon API’lerine erişim olmadan, tam "taralı alan"
 * eğrisini programatik olarak çıkarmak kütüphane sürüm kısıtları nedeniyle mümkün değildir.
 * Bu sınıf, rapora referans görseli eklemek için pratik bir alternatiftir.
 */
public class OutputAreaPlotter {

    public static void plotCrispLine(String varName, double crisp, double xMin, double xMax, String fileName) throws IOException {
        int width = 900, height = 300;
        int left = 50, right = width - 20, bottom = height - 40, top = 20;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Eksenler
        g.setColor(Color.DARK_GRAY);
        g.drawLine(left, bottom, right, bottom);
        g.drawLine(left, bottom, left, top);
        g.drawString(varName + " (crisp)", left, top - 2);

        // Crisp çizgisi
        int cx = (int) map(crisp, xMin, xMax, left, right);
        g.setColor(Color.RED);
        g.drawLine(cx, bottom, cx, top);
        g.drawString(String.format("Crisp: %.2f", crisp), cx + 5, top + 12);

        g.dispose();
        ImageIO.write(img, "png", new File(fileName));
    }

    private static double map(double val, double inMin, double inMax, double outMin, double outMax) {
        if (inMax - inMin == 0) return outMin;
        return (val - inMin) / (inMax - inMin) * (outMax - outMin) + outMin;
    }
}