package tr.com.cicekstok.ui.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import tr.com.cicekstok.enumlar.AltTur;
import tr.com.cicekstok.model.Urun;
import tr.com.cicekstok.servis.EnvanterServisi;

import java.util.List;

public class UrunlerController {

    @FXML
    private Label altTurBaslik;

    @FXML
    private TableView<Urun> urunTablosu;

    @FXML
    private TableColumn<Urun, String> adKolon;

    @FXML
    private TableColumn<Urun, String> kategoriKolon;

    @FXML
    private TableColumn<Urun, String> altTurKolon;

    @FXML
    private TableColumn<Urun, Integer> stokKolon;

    /**
     * KategoriController bu ekrana geçmeden önce seçili alt türü buraya set eder.
     */
    private static AltTur seciliAltTur;

    private final EnvanterServisi servis =
            tr.com.cicekstok.UygulamaBaglami.servis;

    /** KategoriController burayı çağırır */
    public static void altTurAyarla(AltTur altTur) {
        seciliAltTur = altTur;
    }

    @FXML
    public void initialize() {
        // Güvenlik: Eğer bir şekilde alt tür seçilmeden geldiysek ekranı boş göster.
        if (seciliAltTur == null) {
            altTurBaslik.setText("Ürünler");
            urunTablosu.setItems(FXCollections.emptyObservableList());
            return;
        }

        // Örneğin "GUL_DALI" -> "GUL DALI"
        altTurBaslik.setText(seciliAltTur.toString().replace("_", " "));

        // Sütun - alan eşleşmeleri
        adKolon.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getAd()));

        kategoriKolon.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue()
                                .getKategori()
                                .toString()
                                .replace("_", " ")
                ));

        altTurKolon.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue()
                                .getAltTur()
                                .toString()
                                .replace("_", " ")
                ));

        stokKolon.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getStok()).asObject());

        urunleriYukle();
    }

    private void urunleriYukle() {
        List<Urun> liste = servis.altTurListe(seciliAltTur);
        urunTablosu.setItems(FXCollections.observableArrayList(liste));
    }
}
