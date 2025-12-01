package tr.com.cicekstok.enumlar;

public enum KurutulmusCicekTuru implements AltTur {

    LAGURUS("Lagurus"),
    LAVANTA("Lavanta"),
    BUGDAY_BASI("Buğday Başı"),
    PAMPAS("Pampas");

    private final String ad;

    KurutulmusCicekTuru(String ad) {
        this.ad = ad;
    }

    @Override
    public String getAd() {
        return ad;
    }
}
