package pkt;

import java.net.URISyntaxException;
import java.util.Scanner;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        
        System.out.print("Tecrübe (yıl): ");
        double tecrube = in.nextDouble();

        System.out.print("Cinsiyet (0 = Kadın, 1 = Erkek): ");
        double cinsiyet = in.nextDouble();

        System.out.print("Yaş (yıl): ");
        double yas = in.nextDouble();

        try {
            CalisanUretim model = new CalisanUretim(tecrube, cinsiyet, yas);
            //System.out.println(model);
            // üyelik fonksiyonlarını grafik olarak göstermek istersen:
            JFuzzyChart.get().chart(model.getModel());
        } catch (URISyntaxException ex) {
            System.out.println("Hata: " + ex.getMessage());
        }
    }
}
