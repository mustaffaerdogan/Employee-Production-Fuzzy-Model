package pkt;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Random;
import net.sourceforge.jFuzzyLogic.FIS;

public class VeriUretici {

    public static void main(String[] args) {
        try {
            // 1. Create the Fuzzy Logic model instance once
            // We initialize with dummy values, we will update them in the loop
            CalisanUretim fuzzyModel = new CalisanUretim(0, 0, 0);
            FIS fis = fuzzyModel.getModel();

            // 2. Setup file writer
            FileWriter fileWriter = new FileWriter("data_set.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);

            Random random = new Random();
            int dataCount = 4000;

            System.out.println("Veri seti oluşturuluyor... (" + dataCount + " satır)");

            for (int i = 0; i < dataCount; i++) {
                // 3. Generate Random Inputs
                // Experience (Tecrube): 0 to 35
                double tecrube = random.nextDouble() * 35.0;
                
                // Age (Yas): 18 to 60
                // random.nextDouble() gives 0.0 to 1.0
                // we want range size (60 - 18) = 42
                // so: 18 + (random * 42)
                double yas = 18 + (random.nextDouble() * 42.0);
                
                // Gender (Cinsiyet): 0 or 1
                int cinsiyetInt = random.nextInt(2); // 0 or 1
                double cinsiyet = (double) cinsiyetInt;

                // 4. Feed inputs to Fuzzy Logic Model
                fis.setVariable("Tecrube", tecrube);
                fis.setVariable("Cinsiyet", cinsiyet);
                fis.setVariable("Yas", yas);
                
                fis.evaluate();
                
                double parcaSayisi = fis.getVariable("ParcaSayisi").getValue();

                // 5. Normalization
                double normTecrube = tecrube / 35.0;
                double normYas = yas / 60.0;
                double normCinsiyet = cinsiyet; // Already 0 or 1
                double normParcaSayisi = parcaSayisi / 500.0; // Assuming max output is around 500

                // 6. Write to file (Comma separated)
                // Format: Norm_Exp,Norm_Age,Norm_Gender,Norm_Output
                printWriter.printf(java.util.Locale.US, "%.5f,%.5f,%.1f,%.5f%n", 
                                   normTecrube, normYas, normCinsiyet, normParcaSayisi);
            }

            printWriter.close();
            System.out.println("Veri seti başarıyla oluşturuldu: data_set.txt");

        } catch (URISyntaxException e) {
            System.err.println("FCL dosyası yüklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Dosya yazma hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
