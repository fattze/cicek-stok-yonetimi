package tr.com.cicekstok.enumlar;

public enum DolguYesillikTuru implements AltTur {

    EUKALIPTUS("Eukaliptus"),
    RUSKUS("Ruskus"),
    ASFET("Asfet / Asparagus"),
    FERN("Fern"),
    PITIPIT("Pitipit");

    private final String ad;

    DolguYesillikTuru(String ad) {
        this.ad = ad;
    }

    @Override
    public String getAd() {
        return ad;
    }
}
