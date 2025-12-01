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

public class AramaController {

    @FXML private TextField adAraField;
    @FXML private ComboBox<Kategori> kategoriBox;
    @FXML private ComboBox<AltTur> altTurBox;
    @FXML private CheckBox kritikCheck;

    @FXML private TableView<Urun> urunTablosu;
    @FXML private TableColumn<Urun, String> idKolon;
    @FXML private TableColumn<Urun, String> adKolon;
    @FXML private TableColumn<Urun, String> kategoriKolon;
    @FXML private TableColumn<Urun, String> altTurKolon;
    @FXML private TableColumn<Urun, String> birimKolon;
    @FXML private TableColumn<Urun, Integer> stokKolon;
    @FXML private TableColumn<Urun, String> notKolon;

    @FXML private Label ozetLabel;

    private final EnvanterServisi servis =
            tr.com.cicekstok.UygulamaBaglami.servis;

    // kritik stok eşiği
    private static final int KRITIK_ESIK = 10;

    @FXML
    public void initialize() {
        // Kategori doldur
        kategoriBox.getItems().setAll(Kategori.values());
        altTurBox.setDisable(true);

        // 🔹 Kritik stok kutusu değişince otomatik filtre uygula
        kritikCheck.setOnAction(e -> filtreUygula());

        // Tablo kolonları
        idKolon.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getId())
        );

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
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getStok()).asObject()
        );

        notKolon.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getNotlar() == null ? "" : c.getValue().getNotlar()
                )
        );

        // Kritik stok satırlarını renklendir
        urunTablosu.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Urun item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getStok() <= KRITIK_ESIK) {
                    setStyle("-fx-background-color: #ffe6e6;");
                } else {
                    setStyle("");
                }
            }
        });

        // Kategori değişince alt türleri doldur
        kategoriBox.setOnAction(e -> kategoriDegisti());

        // Başlangıçta tüm ürünleri göster
        filtreUygula();
    }

    private void kategoriDegisti() {
        Kategori k = kategoriBox.getValue();
        altTurBox.getSelectionModel().clearSelection();

        if (k == null) {
            altTurBox.getItems().clear();
            altTurBox.setDisable(true);
        } else {
            altTurBox.setDisable(false);
            altTurBox.getItems().setAll(AltTurHaritasi.turleri(k));
        }
    }

    /**
     * Türkçe karakter ve büyük/küçük harf duyarsız karşılaştırma için normalizasyon.
     * Örn:
     *  "ÇiçeK"  -> "cicek"
     *  "şakayık" -> "sakayik"
     */
    private String normalize(String s) {
        if (s == null) return "";
        String lower = s.toLowerCase().trim();

        // Türkçe karakterleri sadeleştir
        lower = lower
                .replace('ç', 'c')
                .replace('ğ', 'g')
                .replace('ı', 'i')
                .replace('ö', 'o')
                .replace('ş', 's')
                .replace('ü', 'u');

        return lower;
    }

    @FXML
    private void filtreUygula() {
        String adFiltreRaw = adAraField.getText();
        String adFiltre = normalize(adFiltreRaw);

        Kategori kategori = kategoriBox.getValue();
        AltTur altTur = altTurBox.getValue();
        boolean sadeceKritik = kritikCheck.isSelected();

        // Tüm ürünlerden başla
        List<Urun> liste = servis.tumunuListele();

        // Kategori filtresi
        if (kategori != null) {
            liste = liste.stream()
                    .filter(u -> u.getKategori() == kategori)
                    .toList();
        }

        // Alt tür filtresi
        if (altTur != null) {
            liste = liste.stream()
                    .filter(u -> u.getAltTur() == altTur)
                    .toList();
        }

        // Ad filtresi (normalize edilmiş karşılaştırma)
        if (!adFiltre.isEmpty()) {
            liste = liste.stream()
                    .filter(u -> {
                        String urunAdiNorm = normalize(u.getAd());
                        return urunAdiNorm.contains(adFiltre);
                    })
                    .toList();
        }

        // Kritik stok filtresi
        if (sadeceKritik) {
            liste = liste.stream()
                    .filter(u -> u.getStok() <= KRITIK_ESIK)
                    .toList();
        }

        urunTablosu.setItems(FXCollections.observableArrayList(liste));
        ozetGuncelle(liste);
    }

    private void ozetGuncelle(List<Urun> liste) {
        int adet = liste.size();
        int toplamStok = liste.stream().mapToInt(Urun::getStok).sum();
        long kritikSayisi = liste.stream()
                .filter(u -> u.getStok() <= KRITIK_ESIK)
                .count();

        ozetLabel.setText(
                "Bulunan ürün: " + adet +
                        " | Toplam stok: " + toplamStok +
                        " | Kritik stokta ürün: " + kritikSayisi
        );
    }

    @FXML
    private void filtreleriTemizle() {
        adAraField.clear();
        kategoriBox.getSelectionModel().clearSelection();
        altTurBox.getItems().clear();
        altTurBox.setDisable(true);
        kritikCheck.setSelected(false);

        filtreUygula();
    }

    @FXML
    private void geriDon() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/ana.fxml", "Çiçek Stok Uygulaması");
    }
}
