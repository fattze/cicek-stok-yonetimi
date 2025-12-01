package tr.com.cicekstok.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tr.com.cicekstok.enumlar.AltTur;
import tr.com.cicekstok.enumlar.AltTurHaritasi;
import tr.com.cicekstok.enumlar.Kategori;
import tr.com.cicekstok.model.Urun;
import tr.com.cicekstok.servis.EnvanterServisi;
import tr.com.cicekstok.ui.util.SceneManager;

import java.util.List;

public class AltTurSilController {

    @FXML private ComboBox<Kategori> kategoriBox;
    @FXML private ComboBox<AltTur> altTurBox;

    private final EnvanterServisi servis =
            tr.com.cicekstok.UygulamaBaglami.servis;

    @FXML
    public void initialize() {
        kategoriBox.getItems().setAll(Kategori.values());
        kategoriBox.setOnAction(e -> kategoriDegisti());
        altTurBox.setDisable(true);
    }

    private void kategoriDegisti() {
        Kategori kategori = kategoriBox.getValue();
        if (kategori == null) {
            altTurBox.getItems().clear();
            altTurBox.setDisable(true);
            return;
        }

        altTurBox.setDisable(false);
        altTurBox.setItems(
                FXCollections.observableArrayList(AltTurHaritasi.turleri(kategori))
        );
    }

    @FXML
    private void sil() {
        Kategori kategori = kategoriBox.getValue();
        AltTur altTur = altTurBox.getValue();

        if (kategori == null) {
            uyari("Lütfen bir kategori seçin.");
            return;
        }
        if (altTur == null) {
            uyari("Lütfen silinecek ürünü seçin.");
            return;
        }

        // Bu alt türde stok var mı kontrol et
        List<Urun> urunler = servis.altTurListe(altTur);
        int toplamStok = 0;
        if (urunler != null) {
            toplamStok = urunler.stream()
                    .mapToInt(Urun::getStok)
                    .sum();
        }

        if (toplamStok > 0) {
            uyari("Bu ürün hâlâ stokta. (Toplam stok: "
                    + toplamStok + ")\nSilmeden önce stokları sıfırlayın.");
            return;
        }

        // Dinamik ise gerçekten kaldır, enum ise gizle
        boolean basarili = AltTurHaritasi.gizleAltTur(kategori, altTur);
        if (!basarili) {
            uyari("Ürün silinemedi.");
            return;
        }

        uyari("Ürün başarıyla silindi.");
        kategoriDegisti(); // listeyi yenile
    }

    @FXML
    private void geriDon() {
        SceneManager.showScene("/tr/com/cicekstok/ui/view/ana.fxml",
                "Çiçek Stok Uygulaması");
    }

    private void uyari(String mesaj) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(mesaj);
        a.showAndWait();
    }
}
