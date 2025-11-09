package pkt;

import java.net.URISyntaxException;
import java.util.Scanner;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
    final boolean showChartsCOG = true;  // COG için grafik
    final boolean showChartsCOA = false; // COA için grafik (kapalı)
        
        System.out.print("Tecrübe (yıl): ");
        double tecrube = in.nextDouble();

        System.out.print("Cinsiyet (0 = Kadın, 1 = Erkek): ");
        double cinsiyet = in.nextDouble();

        System.out.print("Yaş (yıl): ");
        double yas = in.nextDouble();

        try {
            // Varsayılan model (COG)
            CalisanUretim modelCOG = new CalisanUretim(tecrube, cinsiyet, yas);
            System.out.println("[COG] " + modelCOG);
            if (showChartsCOG) JFuzzyChart.get().chart(modelCOG.getModel());

            // Alternatif durulama: COA (Center of Area) — 2. yöntem
            CalisanUretim modelCOA = new CalisanUretim("CalisanUretim_COA.fcl", tecrube, cinsiyet, yas);
            System.out.println("[COA] " + modelCOA);
            if (showChartsCOA) JFuzzyChart.get().chart(modelCOA.getModel());

            // İsteğe bağlı: crisp işaretli basit PNG çıktıları
            //OutputAreaPlotter.plotCrispLine("ParcaSayisi", modelCOG.getModel().getVariable("ParcaSayisi").getValue(), 0, 500, "ParcaSayisi_COG.png");
            //OutputAreaPlotter.plotCrispLine("ParcaSayisi", modelCOA.getModel().getVariable("ParcaSayisi").getValue(), 0, 500, "ParcaSayisi_COA.png");
        } catch (URISyntaxException ex) {
            System.out.println("Hata: " + ex.getMessage());
        }
    }
}
