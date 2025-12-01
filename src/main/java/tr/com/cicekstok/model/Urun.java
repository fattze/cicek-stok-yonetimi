package tr.com.cicekstok.model;

import tr.com.cicekstok.enumlar.*;
import java.util.UUID;

public class Urun {

    // Her ürüne otomatik benzersiz ID
    private String id = UUID.randomUUID().toString();

    private String ad;
    private Kategori kategori;
    private AltTur altTur;

    private Birim birim;
    private int stok;
    private String notlar;

    public Urun() {
    }

    // İstersen kullanmak için pratik bir kurucu
    public Urun(String ad, Kategori kategori, AltTur altTur,
                Birim birim, int stok, String notlar) {
        this.ad = ad;
        this.kategori = kategori;
        this.altTur = altTur;
        this.birim = birim;
        this.stok = stok;
        this.notlar = notlar;
    }

    // ----------------- Getter / Setter -----------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public Kategori getKategori() { return kategori; }
    public void setKategori(Kategori kategori) { this.kategori = kategori; }

    public AltTur getAltTur() { return altTur; }
    public void setAltTur(AltTur altTur) { this.altTur = altTur; }

    public Birim getBirim() { return birim; }
    public void setBirim(Birim birim) { this.birim = birim; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public String getNotlar() { return notlar; }
    public void setNotlar(String notlar) { this.notlar = notlar; }

    @Override
    public String toString() {
        return ad;
    }
}
