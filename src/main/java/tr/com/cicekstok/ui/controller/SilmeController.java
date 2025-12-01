package tr.com.cicekstok.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import tr.com.cicekstok.enumlar.*;
import tr.com.cicekstok.model.Urun;
import tr.com.cicekstok.servis.EnvanterServisi;
import tr.com.cicekstok.ui.util.SceneManager;

import java.util.List;

public class SilmeController {

    @FXML private ComboBox<Kategori> kategoriBox;
    @FXML private ComboBox<AltTur> altTurBox;
    @FXML private ComboBox<Birim> birimBox;      // Birim seçimi
    @FXML private Label mevcutStokLabel;
    @FXML private TextField miktarField;
    @FXML private TextArea notlarField;

    // 🔹 TABLO: sadece görmek için
    @FXML private TableView<Urun> urunTablosu;
    @FXML private TableColumn<Urun, String> adKolon;
    @FXML private TableColumn<Urun, String> kategoriKolon;
    @FXML private TableColumn<Urun, String> altTurKolon;
    @FXML private TableColumn<Urun, String> birimKolon;
    @FXML private TableColumn<Urun, Integer> stokKolon;
    @FXML private TableColumn<Urun, String> notKolon;

    private final EnvanterServisi servis =
            tr.com.cicekstok.UygulamaBaglami.servis;

    // Seçili ürün referansı
    private Urun seciliUrun;

    @FXML
    private void initialize() {
        // Kategorileri doldur
        kategoriBox.getItems().setAll(Kategori.values());

        // Başta alt tür ve birim kapalı
        altTurBox.setDisable(true);
        birimBox.setDisable(true);
        birimBox.getItems().clear();

        mevcutStokLabel.setText("Mevcut stok: -");

        // Kategori değişince alt türleri ve izinli birimleri doldur
        kategoriBox.setOnAction(e -> kategoriDegisti());

        // Alt tür veya birim değişince ürünü yeniden bul
        altTurBox.setOnAction(e -> urunSecimiDegisti());
        birimBox.setOnAction(e -> urunSecimiDegisti());

        // Miktar alanına sadece rakam girilebilsin
        miktarField.textProperty().addListener((obs, eski, yeniDeger) -> {
            if (!yeniDeger.matches("\\d*")) {
                miktarField.setText(yeniDeger.replaceAll("[^\\d]", ""));
            }
        });

        // 🔹 TABLO KOLONLARI
        if (urunTablosu != null) {
            adKolon.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getAd() == null ? "" : c.getValue().getAd()
                    )
            );

            kategoriKolon.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getKategori() == null
                                    ? ""
                                    : c.getValue().getKategori().name().replace("_", " ")
                    )
            );

            altTurKolon.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getAltTur() == null
                                    ? ""
                                    : c.getValue().getAltTur().toString().replace("_", " ")
                    )
            );

            birimKolon.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getBirim() == null
                                    ? ""
                                    : c.getValue().getBirim().name()
                    )
            );

            stokKolon.setCellValueFactory(c ->
                    new SimpleIntegerProperty(c.getValue().getStok()).asObject()
            );

            notKolon.setCellValueFactory(c ->
                    new SimpleStringProperty(
                            c.getValue().getNotlar() == null ? "" : c.getValue().getNotlar()
                    )
            );

            // İlk açılışta tabloyu doldur
            tabloyuYukle();
        }
    }

    private void kategoriDegisti() {
        Kategori k = kategoriBox.getValue();

        // Her şey temizlensin
        seciliUrun = null;
        mevcutStokLabel.setText("Mevcut stok: -");
        altTurBox.getSelectionModel().clearSelection();
        altTurBox.getItems().clear();
        birimBox.getSelectionModel().clearSelection();
        birimBox.getItems().clear();
        birimBox.setDisable(true);

        if (k == null) {
            altTurBox.setDisable(true);
            return;
        }

        // Seçilen kategoriye göre alt türler
        altTurBox.setDisable(false);
        altTurBox.getItems().setAll(AltTurHaritasi.turleri(k));

        // Seçilen kategoriye göre izinli birimler
        birimBox.setDisable(false);
        birimBox.getItems().setAll(BirimKurallari.izinliBirimler(k));
    }

    /**
     * Kategori + AltTür + Birim seçimi değiştiğinde doğru ürünü bulur.
     */
    private void urunSecimiDegisti() {
        seciliUrun = null;
        mevcutStokLabel.setText("Mevcut stok: -");

        Kategori kategori = kategoriBox.getValue();
        AltTur altTur = altTurBox.getValue();
        Birim birim = birimBox.getValue();

        // Seçimler tamamlanmamışsa ürünü arama
        if (kategori == null || altTur == null || birim == null) {
            return;
        }

        // Güvenlik: birim bu kategori için geçerli mi?
        if (!BirimKurallari.birimUygunMu(kategori, birim)) {
            uyari("Seçilen birim bu kategori için geçerli değildir.");
            return;
        }

        // Bu alt türe ait ürünlerden, kategori + alt tür + birim'e uyanı bul
        List<Urun> liste = servis.altTurListe(altTur);
        for (Urun u : liste) {
            if (u.getKategori() == kategori && u.getAltTur() == altTur) {
                Birim mevcutBirim = u.getBirim();

                // 1) Zaten bu birimdeyse -> bu ürün
                // 2) Eski kayıt olup birimi yoksa (null) -> onu da bu ürün kabul ediyoruz
                if (mevcutBirim == null || mevcutBirim.equals(birim)) {
                    seciliUrun = u;
                    break;
                }
            }
        }

        if (seciliUrun == null) {
            uyari("Bu kategori, ürün ve birim için kayıtlı stok bulunamadı.");
        } else {
            // Eski kayıt ise birimini netleştir
            if (seciliUrun.getBirim() == null) {
                seciliUrun.setBirim(birim);
            }
            birimBox.setValue(seciliUrun.getBirim());
            mevcutStokLabel.setText("Mevcut stok: " + seciliUrun.getStok());
        }
    }

    @FXML
    private void azalt() {
        Kategori kategori = kategoriBox.getValue();
        AltTur altTur = altTurBox.getValue();
        Birim secilenBirim = birimBox.getValue();  // ZORUNLU
        String miktarText = miktarField.getText() != null ? miktarField.getText().trim() : "";
        String notlar = notlarField.getText();

        if (kategori == null) {
            uyari("Lütfen bir kategori seçin.");
            return;
        }

        if (altTur == null) {
            uyari("Lütfen ürün seçin.");
            return;
        }

        if (secilenBirim == null) {
            uyari("Lütfen birim seçin.");
            return;
        }

        // Kategori–birim uyumu
        if (!BirimKurallari.birimUygunMu(kategori, secilenBirim)) {
            uyari("Seçilen birim bu kategori için geçerli değildir.");
            return;
        }

        if (seciliUrun == null) {
            uyari("Bu kategori, ürün ve birim için geçerli bir ürün bulunamadı.");
            return;
        }

        // Ek güvenlik: seciliUrun gerçekten bu seçime ait mi?
        if (seciliUrun.getKategori() != kategori ||
                seciliUrun.getAltTur() != altTur ||
                (seciliUrun.getBirim() != null && !seciliUrun.getBirim().equals(secilenBirim))) {
            uyari("Seçiminizle eşleşen ürün bulunamadı. Lütfen seçimleri kontrol edin.");
            return;
        }

        if (miktarText.isEmpty()) {
            uyari("Azaltılacak miktarı girmelisiniz.");
            return;
        }

        int miktar;
        try {
            miktar = Integer.parseInt(miktarText);
        } catch (NumberFormatException ex) {
            uyari("Lütfen miktar alanına geçerli bir sayı girin.");
            return;
        }

        if (miktar <= 0) {
            uyari("Azaltılacak miktar pozitif olmalıdır.");
            return;
        }

        if (seciliUrun.getStok() < miktar) {
            uyari("Yetersiz stok! Mevcut stok: " + seciliUrun.getStok());
            return;
        }

        // Stoku azalt
        seciliUrun.setStok(seciliUrun.getStok() - miktar);

        // Not eklenmişse, eski notların sonuna ekle
        if (notlar != null && !notlar.isBlank()) {
            String eski = seciliUrun.getNotlar();
            if (eski == null || eski.isBlank()) {
                seciliUrun.setNotlar(notlar.trim());
            } else {
                seciliUrun.setNotlar(eski + " | " + notlar.trim());
            }
        }

        // Kaydet
        servis.urunEkle(seciliUrun);   // var olan ürünü güncellemek için de kullanıyoruz

        mevcutStokLabel.setText("Mevcut stok: " + seciliUrun.getStok());
        uyari("Stok başarıyla azaltıldı.");

        // 🔹 Tabloyu yenile ki gözünle değişimi gör
        tabloyuYukle();

        // Formu kısmen temizle
        miktarField.clear();
        notlarField.clear();
        // birimBox'ı temizlemiyoruz; aynı üründen peş peşe işlem yaparken rahat olur.
    }

    // 🔹 Basit: tüm ürünleri tabloya bas (istersen stok=0'ları sonra filtreleriz)
    private void tabloyuYukle() {
        if (urunTablosu == null) return;
        List<Urun> liste = servis.tumunuListele();
        urunTablosu.setItems(FXCollections.observableArrayList(liste));
    }

    @FXML
    private void geriDon() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/ana.fxml", "Çiçek Stok Uygulaması");
    }

    private void uyari(String mesaj) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(mesaj);
        a.showAndWait();
    }
}
