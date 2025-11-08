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

    public FIS getModel() {
        return fis;
    }

    @Override
    public String toString() {
        double parcaSayisi = fis.getVariable("ParcaSayisi").getValue();
        return "Tahmini Üretilen Parça Sayısı: " + Math.round(parcaSayisi);
    }
}
