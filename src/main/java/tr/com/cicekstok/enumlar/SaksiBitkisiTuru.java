package tr.com.cicekstok.enumlar;

public enum SaksiBitkisiTuru implements AltTur {

    KAKTUS("Kaktüs"),
    BONSAY("Bonsai"),
    ORKIDE_BITKISI("Orkide Bitkisi"),
    SARMAŞIK("Sarmaşık"),
    SALON_BITKISI("Salon Bitkisi");

    private final String ad;

    SaksiBitkisiTuru(String ad) {
        this.ad = ad;
    }

    @Override
    public String getAd() {
        return ad;
    }
}
