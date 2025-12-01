package tr.com.cicekstok.ui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tr.com.cicekstok.UygulamaBaglami;
import tr.com.cicekstok.enumlar.AltTurHaritasi;
import tr.com.cicekstok.enumlar.Kategori;
import tr.com.cicekstok.model.Urun;
import tr.com.cicekstok.servis.EnvanterServisi;
import tr.com.cicekstok.ui.util.SceneManager;

import java.net.URL;
import java.util.List;
import java.util.Locale;

public class AnaController {

    // Üst kısım
    @FXML private Label hosgeldinLabel;
    @FXML private Label toplamStokLabel;
    @FXML private Label kritikTurLabel;
    @FXML private Label toplamUrunLabel;
    @FXML private Label toplamAltTurLabel;
    @FXML private PieChart stokOzetChart;

    // Kategori kartlarındaki stok etiketleri
    @FXML private Label topKesme;
    @FXML private Label topSaksi;
    @FXML private Label topAksesuar;
    @FXML private Label topDolgu;
    @FXML private Label topKurutulmus;
    @FXML private Label topHediyelik;
    @FXML private Label topHazir;

    // Kategori kartlarındaki resimler
    @FXML private ImageView imgKesme;
    @FXML private ImageView imgSaksi;
    @FXML private ImageView imgAksesuar;
    @FXML private ImageView imgDolgu;
    @FXML private ImageView imgKurutulmus;
    @FXML private ImageView imgHediyelik;
    @FXML private ImageView imgHazir;

    private final EnvanterServisi servis = UygulamaBaglami.servis;

    private static final int KRITIK_ESIK = 10;

    @FXML
    public void initialize() {
        // Hoş geldiniz yazısı
        String aktif = UygulamaBaglami.aktifKullanici;
        if (aktif != null && !aktif.isBlank()) {
            hosgeldinLabel.setText("Hoş geldiniz, " + aktif);
        } else {
            hosgeldinLabel.setText("");
        }

        // Tüm ürünler
        List<Urun> tumUrunler = servis.tumunuListele();

        int toplamStok = tumUrunler.stream()
                .mapToInt(Urun::getStok)
                .sum();

        int toplamUrun = tumUrunler.size();

        long kritikUrunSayisi = tumUrunler.stream()
                .filter(u -> u.getStok() <= KRITIK_ESIK)
                .count();

        int toplamAltTur = 0;
        for (Kategori k : Kategori.values()) {
            toplamAltTur += AltTurHaritasi.turleri(k).length;
        }

        toplamStokLabel.setText("Toplam Stok: " + toplamStok);
        toplamUrunLabel.setText("Stok Bilgisi Girilmiş Toplam Ürün Sayısı: " + toplamUrun);
        kritikTurLabel.setText("Kritik Stokta Ürün Sayısı: " + kritikUrunSayisi);
        toplamAltTurLabel.setText("Toplam Ürün Sayısı: " + toplamAltTur);

        // Kategori bazlı stoklar
        int kesmeStok = stokToplamKategori(Kategori.KESME_CICEK);
        int saksiStok = stokToplamKategori(Kategori.SAKSI_BITKISI);
        int aksesuarStok = stokToplamKategori(Kategori.AKSESUAR);
        int dolguStok = stokToplamKategori(Kategori.DOLGU_YESILLIK);
        int kurutulmusStok = stokToplamKategori(Kategori.KURUTULMUS_CICEK);
        int hediyelikStok = stokToplamKategori(Kategori.HEDIYELIK);
        int hazirStok = stokToplamKategori(Kategori.HAZIR_URUN);

        // Kart etiketleri
        topKesme.setText("Stok: " + kesmeStok);
        topSaksi.setText("Stok: " + saksiStok);
        topAksesuar.setText("Stok: " + aksesuarStok);
        topDolgu.setText("Stok: " + dolguStok);
        topKurutulmus.setText("Stok: " + kurutulmusStok);
        topHediyelik.setText("Stok: " + hediyelikStok);
        topHazir.setText("Stok: " + hazirStok);

        // Pasta grafik
        stokOzetChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Kesme Çiçek", kesmeStok),
                new PieChart.Data("Saksı Bitkisi", saksiStok),
                new PieChart.Data("Aksesuar", aksesuarStok),
                new PieChart.Data("Dolgu Yeşillik", dolguStok),
                new PieChart.Data("Kurutulmuş Çiçek", kurutulmusStok),
                new PieChart.Data("Hediyelik", hediyelikStok),
                new PieChart.Data("Hazır Ürün", hazirStok)
        ));

        // Kategori resimleri (sabit)
        kategoriResmiAyarla(Kategori.KESME_CICEK, imgKesme);
        kategoriResmiAyarla(Kategori.SAKSI_BITKISI, imgSaksi);
        kategoriResmiAyarla(Kategori.AKSESUAR, imgAksesuar);
        kategoriResmiAyarla(Kategori.DOLGU_YESILLIK, imgDolgu);
        kategoriResmiAyarla(Kategori.KURUTULMUS_CICEK, imgKurutulmus);
        kategoriResmiAyarla(Kategori.HEDIYELIK, imgHediyelik);
        kategoriResmiAyarla(Kategori.HAZIR_URUN, imgHazir);
    }

    private int stokToplamKategori(Kategori kategori) {
        return servis.kategoriListe(kategori).stream()
                .mapToInt(Urun::getStok)
                .sum();
    }

    /**
     * Her kategori için sabit resmi img klasöründen yükler.
     * Örn: KESME_CICEK -> /tr/com/cicekstok/img/kesme_cicek.png
     */
    private void kategoriResmiAyarla(Kategori kategori, ImageView hedef) {
        if (hedef == null) return;

        String kod = kategori.name().toLowerCase(Locale.ROOT); // kesme_cicek, saksi_bitkisi, ...
        String basePath = "/tr/com/cicekstok/img/" + kod;

        URL resUrl = null;
        for (String ext : new String[]{".png", ".jpg", ".jpeg"}) {
            resUrl = getClass().getResource(basePath + ext);
            if (resUrl != null) break;
        }

        if (resUrl == null) {
            System.out.println("Kategori resmi bulunamadı: " + basePath + "[.png/.jpg/.jpeg]");
            return;
        }

        Image img = new Image(resUrl.toExternalForm());
        hedef.setImage(img);
        hedef.setFitWidth(60);
        hedef.setPreserveRatio(true);
    }

    // --- Kategori kartı tıklamaları ---

    @FXML
    private void kesmeCicekTiklandi() {
        KategoriController.kategoriAyarla(Kategori.KESME_CICEK);
        SceneManager.showScene("/tr/com/cicekstok/ui/view/kategori.fxml", "Kesme Çiçek");
    }

    @FXML
    private void saksiBitkisiTiklandi() {
        KategoriController.kategoriAyarla(Kategori.SAKSI_BITKISI);
        SceneManager.showScene("/tr/com/cicekstok/ui/view/kategori.fxml", "Saksı Bitkisi");
    }

    @FXML
    private void aksesuarTiklandi() {
        KategoriController.kategoriAyarla(Kategori.AKSESUAR);
        SceneManager.showScene("/tr/com/cicekstok/ui/view/kategori.fxml", "Aksesuar");
    }

    @FXML
    private void dolguYesillikTiklandi() {
        KategoriController.kategoriAyarla(Kategori.DOLGU_YESILLIK);
        SceneManager.showScene("/tr/com/cicekstok/ui/view/kategori.fxml", "Dolgu Yeşillik");
    }

    @FXML
    private void kurutulmusCicekTiklandi() {
        KategoriController.kategoriAyarla(Kategori.KURUTULMUS_CICEK);
        SceneManager.showScene("/tr/com/cicekstok/ui/view/kategori.fxml", "Kurutulmuş Çiçek");
    }

    @FXML
    private void hediyelikTiklandi() {
        KategoriController.kategoriAyarla(Kategori.HEDIYELIK);
        SceneManager.showScene("/tr/com/cicekstok/ui/view/kategori.fxml", "Hediyelik");
    }

    @FXML
    private void hazirUrunTiklandi() {
        KategoriController.kategoriAyarla(Kategori.HAZIR_URUN);
        SceneManager.showScene("/tr/com/cicekstok/ui/view/kategori.fxml", "Hazır Ürün");
    }

    // --- Sol menü butonları ---

    @FXML
    private void anaEkran() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/ana.fxml", "Çiçek Stok Uygulaması");
    }

    @FXML
    private void aramaEkrani() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/arama.fxml", "Arama");
    }

    @FXML
    private void eklemeEkrani() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/ekleme.fxml", "Stok Ekle");
    }

    @FXML
    private void silmeEkrani() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/silme.fxml", "Stok Azalt");
    }

    @FXML
    private void guncellemeEkrani() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/guncelleme.fxml", "Güncelleme");
    }

    @FXML
    private void yeniUrunEkrani() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/alttur_ekle.fxml", "Yeni Ürün Ekle");
    }

    @FXML
    private void altTurSilEkrani() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/alttur_sil.fxml", "Ürün Sil");
    }

    @FXML
    private void cikisYap() {
        SceneManager.showScene(
                "/tr/com/cicekstok/ui/view/giris.fxml",
                "Çiçek Stok Yönetimi - Giriş"
        );
    }
}
