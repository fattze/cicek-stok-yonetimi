package tr.com.cicekstok.enumlar;

public enum HediyelikTuru implements AltTur {

    KUPA("Kupa"),
    PELUS("Peluş"),
    CERCEVE("Çerçeve"),
    KUTU("Kutu"),
    KOKULU_TAS("Kokulu Taş");

    private final String ad;

    HediyelikTuru(String ad) {
        this.ad = ad;
    }

    @Override
    public String getAd() {
        return ad;
    }
}
