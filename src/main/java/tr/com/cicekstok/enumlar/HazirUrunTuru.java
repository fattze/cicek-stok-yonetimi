package tr.com.cicekstok.enumlar;

public enum HazirUrunTuru implements AltTur {

    BUKET("Buket"),
    ARANJMAN("Aranjman"),
    GELIN_CICEGI("Gelin Çiçeği"),
    MINI_ARAMA("Mini Aranjman"),
    SERAMIK_ARAMA("Seramik Aranjman");

    private final String ad;

    HazirUrunTuru(String ad) {
        this.ad = ad;
    }

    @Override
    public String getAd() {
        return ad;
    }
}
