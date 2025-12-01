package tr.com.cicekstok.ui.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tr.com.cicekstok.enumlar.*;
import tr.com.cicekstok.model.Urun;
import tr.com.cicekstok.servis.EnvanterServisi;
import tr.com.cicekstok.ui.util.SceneManager;

import java.util.List;

public class EklemeController {

    @FXML private ComboBox<Kategori> kategoriBox;
    @FXML private ComboBox<AltTur> altTurBox;
    @FXML private ComboBox<Birim> birimBox;
    @FXML private TextField stokField;
    @FXML private TextArea notlarField;

    // TABLO
    @FXML private TableView<Urun> urunTablosu;
    @FXML private TableColumn<Urun, String> adKolon;
    @FXML private TableColumn<Urun, String> kategoriKolon;
    @FXML private TableColumn<Urun, String> altTurKolon;
    @FXML private TableColumn<Urun, String> birimKolon;
    @FXML private TableColumn<Urun, Integer> stokKolon;
    @FXML private TableColumn<Urun, String> notKolon;

    private final EnvanterServisi servis =
            tr.com.cicekstok.UygulamaBaglami.servis;

    @FXML
    private void initialize() {
        // Kategori seçenekleri
        kategoriBox.getItems().setAll(Kategori.values());

        // Başta alt tür ve birim devre dışı
        altTurBox.setDisable(true);
        birimBox.setDisable(true);
        birimBox.getItems().clear();

        // Kategori değişince sadece alt tür ve birim listelerini güncelle
        kategoriBox.setOnAction(e -> kategoriDegisti());

        // Stok alanına sadece rakam girilebilsin
        stokField.textProperty().addListener((obs, eski, yeniDeger) -> {
            if (!yeniDeger.matches("\\d*")) {
                stokField.setText(yeniDeger.replaceAll("[^\\d]", ""));
            }
        });

        // TABLO KOLONLARI
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

        // Tablodan seçim yapılınca formu doldur (istersen bunu da kullanırsın)
        urunTablosu.getSelectionModel().selectedItemProperty().addListener(
                (obs, eski, yeni) -> tabloSecildi(yeni)
        );

        // İlk açılışta TÜM ürünleri göster
        tabloyuYukle();
    }

    @FXML
    private void kategoriDegisti() {
        Kategori k = kategoriBox.getValue();

        if (k == null) {
            // Alt tür temizle
            altTurBox.getItems().clear();
            altTurBox.setDisable(true);

            // Birim temizle
            birimBox.getItems().clear();
            birimBox.getSelectionModel().clearSelection();
            birimBox.setDisable(true);
            return;
        }

        // Seçilen kategoriye göre alt türleri doldur
        altTurBox.setDisable(false);
        altTurBox.getItems().setAll(AltTurHaritasi.turleri(k));
        altTurBox.getSelectionModel().clearSelection();

        // Seçilen kategoriye göre izinli birimleri doldur
        birimBox.setDisable(false);
        birimBox.getSelectionModel().clearSelection();
        birimBox.getItems().setAll(BirimKurallari.izinliBirimler(k));
    }

    /**
     * Tabloyu TÜM ürünlerle doldurur.
     */
    private void tabloyuYukle() {
        List<Urun> liste = servis.tumunuListele();
        urunTablosu.setItems(FXCollections.observableArrayList(liste));
    }

    /**
     * Tablodan ürün seçilince formu doldurur.
     * (Bu kalsın, kullanışlı. İstersen kaldırabiliriz.)
     */
    private void tabloSecildi(Urun urun) {
        if (urun == null) {
            return;
        }

        // Kategori
        if (urun.getKategori() != null) {
            kategoriBox.setValue(urun.getKategori());

            // Alt türleri güncelle
            altTurBox.setDisable(false);
            altTurBox.getItems().setAll(AltTurHaritasi.turleri(urun.getKategori()));
        }

        // Alt tür
        if (urun.getAltTur() != null) {
            altTurBox.setValue(urun.getAltTur());
        }

        // Birim
        birimBox.setDisable(false);
        birimBox.getItems().setAll(BirimKurallari.izinliBirimler(urun.getKategori()));

        if (urun.getBirim() != null &&
                BirimKurallari.birimUygunMu(urun.getKategori(), urun.getBirim())) {
            birimBox.setValue(urun.getBirim());
        } else {
            birimBox.getSelectionModel().clearSelection();
        }

        // Notlar alanını istersen doldur, istersen boş bırak;
        // ben mevcut notları gösteriyorum, sen üzerine yeni not ekleyebilirsin.
        notlarField.setText(urun.getNotlar() == null ? "" : urun.getNotlar());

        // Miktar alanı boş kalsın; kullanıcı ne kadar ekleyeceğini yazsın.
        stokField.clear();
    }

    @FXML
    private void kaydet() {
        Kategori kategori = kategoriBox.getValue();
        AltTur altTur = altTurBox.getValue();
        Birim birim = birimBox.getValue(); // ZORUNLU
        String stokText = stokField.getText() != null ? stokField.getText().trim() : "";
        String notlar = notlarField.getText();

        // --- VALIDASYONLAR ---

        if (kategori == null) {
            uyari("Lütfen bir kategori seçin.");
            return;
        }

        if (altTur == null) {
            uyari("Lütfen bir alt tür seçin.");
            return;
        }

        if (birim == null) {
            uyari("Lütfen birim seçin.");
            return;
        }

        // EK GÜVENLİK: Seçilen birim bu kategori için izinli mi?
        if (!BirimKurallari.birimUygunMu(kategori, birim)) {
            uyari("Seçilen birim bu kategori için geçerli değildir.");
            return;
        }

        if (stokText.isEmpty()) {
            uyari("Eklenecek miktarı girmelisiniz.");
            return;
        }

        int miktar;
        try {
            miktar = Integer.parseInt(stokText);
        } catch (NumberFormatException ex) {
            uyari("Lütfen miktar alanına geçerli bir sayı girin.");
            return;
        }

        if (miktar <= 0) {
            uyari("Eklenecek miktar pozitif olmalıdır.");
            return;
        }

        // --- BU ALT TÜR + KATEGORİ + BİRİMDE ÜRÜN VAR MI? ---

        List<Urun> liste = servis.altTurListe(altTur);
        Urun bulunan = null;

        for (Urun u : liste) {
            if (u.getKategori() == kategori && u.getAltTur() == altTur) {
                Birim mevcutBirim = u.getBirim();

                // 1) Zaten aynı birimdeyse -> bu ürün
                // 2) Eski kayıt olup birimi yoksa -> onu da bu ürün say
                if (mevcutBirim == null || mevcutBirim.equals(birim)) {
                    bulunan = u;
                    break;
                }
            }
        }

        if (bulunan != null) {
            // MEVCUT ÜRÜNE STOK EKLE
            bulunan.setStok(bulunan.getStok() + miktar);

            // Eski kayıt ise birimini de netleştir
            bulunan.setBirim(birim);

            // Not eklenmişse mevcut notların sonuna ekle
            if (notlar != null && !notlar.isBlank()) {
                String eski = bulunan.getNotlar();
                if (eski == null || eski.isBlank()) {
                    bulunan.setNotlar(notlar.trim());
                } else {
                    bulunan.setNotlar(eski + " | " + notlar.trim());
                }
            }

            servis.urunEkle(bulunan);
            uyari("Stok güncellendi.");

        } else {
            // YENİ ÜRÜN OLUŞTUR
            Urun yeni = new Urun();
            yeni.setKategori(kategori);
            yeni.setAltTur(altTur);

            // Ürün adını alt tür isminden üret (ör: GUL_SEDAF -> "GUL SEDAF")
            String ad = altTur.toString().replace("_", " ");
            yeni.setAd(ad);

            yeni.setStok(miktar);
            yeni.setBirim(birim); // validasyondan geçtiği için null değil

            if (notlar != null && !notlar.isBlank()) {
                yeni.setNotlar(notlar.trim());
            }

            servis.urunEkle(yeni);
            uyari("Yeni ürün oluşturuldu ve stok eklendi.");
        }

        // Formu temizle
        stokField.clear();
        notlarField.clear();
        birimBox.getSelectionModel().clearSelection();

        // 🔹 Tabloyu tam listeyle yenile -> eklenen/güncellenen ürünü gözünle gör
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
