package pkt;

import java.net.URISyntaxException;

public class Scenarios {
    private static void runScenario(double tecrube, double cinsiyet, double yas) throws URISyntaxException {
        CalisanUretim cog = new CalisanUretim(tecrube, cinsiyet, yas);
        CalisanUretim coa = new CalisanUretim("CalisanUretim_COA.fcl", tecrube, cinsiyet, yas);
        System.out.printf("Tecrube=%.2f, Cinsiyet=%.0f, Yas=%.0f => COG=%s, COA=%s%n",
                tecrube, cinsiyet, yas, cog.toString(), coa.toString());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Ornek Senaryolar (COG vs COA):");
        runScenario(5, 1, 30);
        runScenario(2, 0, 22);
        runScenario(12, 1, 35);
        runScenario(0, 0, 55);
        runScenario(20, 1, 50);
    }
}