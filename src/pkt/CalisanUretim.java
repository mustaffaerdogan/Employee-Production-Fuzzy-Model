package pkt;

import java.io.File;
import java.net.URISyntaxException;
import net.sourceforge.jFuzzyLogic.FIS;

public class CalisanUretim {
    private final FIS fis;
    private final double tecrube;
    private final double cinsiyet;
    private final double yas;
    
    public CalisanUretim(double tecrube, double cinsiyet, double yas) throws URISyntaxException {
        this.tecrube = tecrube;
        this.cinsiyet = cinsiyet;
        this.yas = yas;

        File file = new File(getClass().getResource("CalisanUretim.fcl").toURI());
        fis = FIS.load(file.getPath(), true);

        fis.setVariable("Tecrube", tecrube);
        fis.setVariable("Cinsiyet", cinsiyet);
        fis.setVariable("Yas", yas);
        fis.evaluate();
    }

    // Alternatif FCL dosyası ile model oluşturma (ör. MOM, LOM)
    public CalisanUretim(String fclResourceName, double tecrube, double cinsiyet, double yas) throws URISyntaxException {
        this.tecrube = tecrube;
        this.cinsiyet = cinsiyet;
        this.yas = yas;
        // Kaynak dosyayı önce sınıfpath üzerinden dene, sonra alternatif yolları kontrol et
        File file;
        try {
            file = new File(getClass().getResource(fclResourceName).toURI());
        } catch (NullPointerException e) {
            // Fallback: src veya bin içindeki paket yolundan dene
            File trySrc = new File("src/pkt/" + fclResourceName);
            File tryBin = new File("bin/pkt/" + fclResourceName);
            if (trySrc.exists()) file = trySrc; else file = tryBin;
        }
        fis = FIS.load(file.getPath(), true);

        fis.setVariable("Tecrube", tecrube);
        fis.setVariable("Cinsiyet", cinsiyet);
        fis.setVariable("Yas", yas);
        fis.evaluate();
    }

    public FIS getModel() {
        return fis;
    }

    @Override
    public String toString() {
        double parcaSayisi = fis.getVariable("ParcaSayisi").getValue();
        return "Tahmini Üretilen Parça Sayısı: " + Math.round(parcaSayisi);
    }
}
