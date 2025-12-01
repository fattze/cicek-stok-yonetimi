package tr.com.cicekstok.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tr.com.cicekstok.enumlar.*;
import tr.com.cicekstok.model.Urun;
import tr.com.cicekstok.servis.EnvanterServisi;
import tr.com.cicekstok.ui.util.SceneManager;

import java.util.List;
import java.util.Objects;

public class GuncellemeController {

    @FXML private TextField aramaField;

    @FXML private TableView<Urun> urunTablosu;
    @FXML private TableColumn<Urun, String> adKolon;
    @FXML private TableColumn<Urun, String> kategoriKolon;
    @FXML private TableColumn<Urun, String> altTurKolon;
    @FXML private TableColumn<Urun, String> birimKolon;
    @FXML private TableColumn<Urun, Integer> stokKolon;
    @FXML private TableColumn<Urun, String> notlarKolon;   // ✅ YENİ: Notlar sütunu

    @FXML private TextField adField;
    @FXML private ComboBox<Kategori> kategoriBox;
    @FXML private ComboBox<AltTur> altTurBox;
    @FXML private ComboBox<Birim> birimBox;
    @FXML private TextField stokField;
    @FXML private TextArea notlarField;

    private final EnvanterServisi servis =
            tr.com.cicekstok.UygulamaBaglami.servis;

    private Urun seciliUrun;

    @FXML
    public void initialize() {
        // Combobox'lar
        kategoriBox.getItems().setAll(Kategori.values());

        // Alt tür ve birim başta kapalı
        altTurBox.setDisable(true);
        birimBox.setDisable(true);
        birimBox.getItems().clear();

        // Tablo kolonları
        adKolon.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        Objects.toString(c.getValue().getAd(), "")
                )
        );

        kategoriKolon.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getKategori() == null
                                ? ""
                                : c.getValue().getKategori().name().replace("_", " ")
                )
        );

        altTurKolon.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getAltTur() == null
                                ? ""
                                : c.getValue().getAltTur().toString().replace("_", " ")
                )
        );

        birimKolon.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getBirim() == null
                                ? ""
                                : c.getValue().getBirim().name()
                )
        );

        stokKolon.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(
                        c.getValue().getStok()
                ).asObject()
        );

        // ✅ NOTLAR sütunu: kayıtlı notları tabloda göster
        notlarKolon.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getNotlar() == null
                                ? ""
                                : c.getValue().getNotlar()
                )
        );

        // Tablo seçim dinleyicisi
        urunTablosu.getSelectionModel().selectedItemProperty().addListener(
                (obs, eski, yeni) -> tabloSecildi(yeni)
        );

        // Kategori değişince alt tür + izinli birimler
        kategoriBox.setOnAction(e -> kategoriDegisti());

        // Stok alanına sadece sayı
        stokField.textProperty().addListener((obs, eski, yeniDeger) -> {
            if (!yeniDeger.matches("\\d*")) {
                stokField.setText(yeniDeger.replaceAll("[^\\d]", ""));
            }
        });

        // İlk açılışta tüm ürünler
        tabloyuYukle();
    }

    /**
     * Türkçe karakter ve büyük/küçük harf duyarsız karşılaştırma için normalizasyon.
     */
    private String normalize(String s) {
        if (s == null) return "";
        String lower = s.toLowerCase().trim();

        lower = lower
                .replace('ç', 'c')
                .replace('ğ', 'g')
                .replace('ı', 'i')
                .replace('ö', 'o')
                .replace('ş', 's')
                .replace('ü', 'u');

        return lower;
    }

    private void tabloyuYukle() {
        String filtreRaw = aramaField.getText();
        String filtre = normalize(filtreRaw);

        List<Urun> liste = servis.tumunuListele();

        liste = liste.stream()
                // 1) STOK > 0 OLANLARI AL
                .filter(u -> u.getStok() > 0)
                // 2) İsim filtresi (arama kutusu boş değilse)
                .filter(u -> {
                    if (filtre.isEmpty()) return true;
                    String urunAdiNorm = normalize(u.getAd());
                    return urunAdiNorm.contains(filtre);
                })
                .toList();

        urunTablosu.setItems(FXCollections.observableArrayList(liste));
    }


    private void tabloSecildi(Urun urun) {
        seciliUrun = urun;
        if (urun == null) {
            adField.clear();
            kategoriBox.getSelectionModel().clearSelection();

            altTurBox.getItems().clear();
            altTurBox.setDisable(true);

            birimBox.getItems().clear();
            birimBox.getSelectionModel().clearSelection();
            birimBox.setDisable(true);

            stokField.clear();
            notlarField.clear();
            return;
        }

        adField.setText(urun.getAd());
        kategoriBox.setValue(urun.getKategori());

        if (urun.getKategori() != null) {
            // Alt türleri yükle
            altTurBox.setDisable(false);
            altTurBox.getItems().setAll(AltTurHaritasi.turleri(urun.getKategori()));
            altTurBox.setValue(urun.getAltTur());

            // ✅ Kategoriye göre izinli birimleri yükle
            birimBox.setDisable(false);
            List<Birim> izinli = BirimKurallari.izinliBirimler(urun.getKategori());
            birimBox.getItems().setAll(izinli);

            // Ürünün birimi izinli ise seç, değilse boş bırak
            if (urun.getBirim() != null && izinli.contains(urun.getBirim())) {
                birimBox.setValue(urun.getBirim());
            } else {
                birimBox.getSelectionModel().clearSelection();
            }
        } else {
            altTurBox.getItems().clear();
            altTurBox.setDisable(true);

            birimBox.getItems().clear();
            birimBox.getSelectionModel().clearSelection();
            birimBox.setDisable(true);
        }

        stokField.setText(Integer.toString(urun.getStok()));
        notlarField.setText(urun.getNotlar() == null ? "" : urun.getNotlar());
    }

    @FXML
    private void kategoriDegisti() {
        Kategori k = kategoriBox.getValue();

        if (k == null) {
            altTurBox.getItems().clear();
            altTurBox.setDisable(true);

            birimBox.getItems().clear();
            birimBox.getSelectionModel().clearSelection();
            birimBox.setDisable(true);
            return;
        }

        // Alt türleri doldur
        altTurBox.setDisable(false);
        altTurBox.getItems().setAll(AltTurHaritasi.turleri(k));

        if (seciliUrun != null && seciliUrun.getKategori() == k && seciliUrun.getAltTur() != null) {
            if (altTurBox.getItems().contains(seciliUrun.getAltTur())) {
                altTurBox.setValue(seciliUrun.getAltTur());
            }
        } else {
            altTurBox.getSelectionModel().clearSelection();
        }

        // ✅ Kategoriye göre izinli birimleri doldur
        birimBox.setDisable(false);
        birimBox.getItems().setAll(BirimKurallari.izinliBirimler(k));
        birimBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void ara() {
        tabloyuYukle();
    }

    @FXML
    private void guncelle() {
        if (seciliUrun == null) {
            uyari("Lütfen tablodan güncellenecek bir ürün seçin.");
            return;
        }

        String ad = adField.getText() == null ? "" : adField.getText().trim();
        Kategori kategori = kategoriBox.getValue();
        AltTur altTur = altTurBox.getValue();
        Birim birim = birimBox.getValue();
        String stokText = stokField.getText() == null ? "" : stokField.getText().trim();
        String notlar = notlarField.getText();

        if (ad.isEmpty()) {
            uyari("Ürün adı boş bırakılamaz.");
            return;
        }
        if (kategori == null) {
            uyari("Kategori seçmelisiniz.");
            return;
        }
        if (altTur == null) {
            uyari("Ürün seçmelisiniz.");
            return;
        }
        // ✅ Birim zorunlu
        if (birim == null) {
            uyari("Birim seçmelisiniz.");
            return;
        }
        // ✅ Seçilen birim bu kategori için izinli mi?
        if (!BirimKurallari.birimUygunMu(kategori, birim)) {
            uyari("Seçilen birim bu kategori için geçerli değildir.");
            return;
        }

        int stok;
        try {
            stok = Integer.parseInt(stokText);
        } catch (NumberFormatException e) {
            uyari("Stok alanına geçerli bir sayı girin.");
            return;
        }

        if (stok < 0) {
            uyari("Stok negatif olamaz.");
            return;
        }

        seciliUrun.setAd(ad);
        seciliUrun.setKategori(kategori);
        seciliUrun.setAltTur(altTur);
        seciliUrun.setBirim(birim);
        seciliUrun.setStok(stok);
        seciliUrun.setNotlar(notlar == null || notlar.isBlank() ? null : notlar.trim());

        try {
            servis.urunEkle(seciliUrun);
        } catch (Exception e) {
            uyari("Güncelleme sırasında hata: " + e.getMessage());
            return;
        }

        uyari("Ürün başarıyla güncellendi.");
        tabloyuYukle();
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
