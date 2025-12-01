package tr.com.cicekstok.enumlar;

public enum AksesuarTuru implements AltTur {

    SUS_KAGIDI("Süs Kağıdı"),
    KURDELE("Kurdele"),
    VAZO("Vazo"),
    TEL("Tel"),
    SICAK_SILIKON("Sıcak Silikon"),
    SPONJ("Sünger / Sponj");

    private final String ad;

    AksesuarTuru(String ad) {
        this.ad = ad;
    }

    @Override
    public String getAd() {
        return ad;
    }
}
