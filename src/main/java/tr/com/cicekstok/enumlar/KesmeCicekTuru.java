package tr.com.cicekstok.enumlar;

public enum KesmeCicekTuru implements AltTur {

    GUL("Gül"),
    LALE("Lale"),
    ORKIDE("Orkide"),
    PAPATYA("Papatya"),
    GERBERA("Gerbera"),
    KARANFIL("Karanfil"),
    GLAYOL("Glayöl");

    private final String ad;

    KesmeCicekTuru(String ad) {
        this.ad = ad;
    }

    @Override
    public String getAd() {
        return ad;
    }
}
