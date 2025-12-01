package tr.com.cicekstok.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import tr.com.cicekstok.enumlar.*;
import tr.com.cicekstok.ui.util.SceneManager;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class AltTurEkleController {

    @FXML private ComboBox<Kategori> kategoriBox;
    @FXML private ComboBox<Birim> birimBox;   // 🔹 YENİ: alt türün birimi
    @FXML private TextField adField;

    @FXML private Label resimLabel;
    @FXML private ImageView onizlemeResim;

    // Kullanıcının seçtiği resim dosyası
    private File secilenResim;

    @FXML
    public void initialize() {
        // Kategori combobox doldur
        kategoriBox.getItems().setAll(Kategori.values());
        if (resimLabel != null) {
            resimLabel.setText("Resim seçilmedi");
        }

        // Başta birim kapalı olsun
        if (birimBox != null) {
            birimBox.setDisable(true);
            birimBox.getItems().clear();
        }

        // Kategori seçilince o kategoriye uygun birimleri doldur
        kategoriBox.setOnAction(e -> kategoriDegisti());

        // 🔹 Alt tür adı: sadece harf, sayı ve '+' kabul et
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();

            // Silme / hareket vb. için izin ver
            if (text == null || text.isEmpty()) {
                return change;
            }

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (!(Character.isLetterOrDigit(c) || c == '+')) {
                    // Geçersiz karakter: değişikliği iptal et
                    return null;
                }
            }
            return change;
        };

        adField.setTextFormatter(new TextFormatter<>(filter));
    }

    private void kategoriDegisti() {
        Kategori kategori = kategoriBox.getValue();

        if (birimBox == null) return;

        birimBox.getSelectionModel().clearSelection();
        birimBox.getItems().clear();

        if (kategori == null) {
            birimBox.setDisable(true);
            return;
        }

        // Bu kategoriye izinli birimleri getir (DAL/DEMET vs.)
        birimBox.setDisable(false);
        birimBox.getItems().setAll(BirimKurallari.izinliBirimler(kategori));
    }

    @FXML
    private void resimSec() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Alt tür için resim seç");

        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg")
        );

        File dosya = fc.showOpenDialog(adField.getScene().getWindow());
        if (dosya != null) {
            secilenResim = dosya;
            resimLabel.setText(dosya.getName());
            onizlemeResim.setImage(new Image(dosya.toURI().toString()));
        }
    }

    @FXML
    private void ekle() {
        Kategori kategori = kategoriBox.getValue();
        Birim birim = birimBox != null ? birimBox.getValue() : null;
        String ad = adField.getText() == null ? "" : adField.getText().trim();

        if (kategori == null) {
            uyari("Lütfen bir kategori seçin.");
            return;
        }
        if (ad.isEmpty()) {
            uyari("Lütfen eklenecek alt tür adını yazın.");
            return;
        }

        // 🔹 Güvenlik için pattern kontrolü (sadece harf, sayı ve '+')
        if (!ad.matches("[\\p{L}\\p{N}+]+")) {
            uyari("Alt tür adı sadece harf, sayı ve '+' içerebilir.");
            return;
        }

        if (secilenResim == null) {
            uyari("Lütfen bu alt tür için bir resim seçin.");
            return;
        }

        // 🔹 Birim zorunlu
        if (birim == null) {
            uyari("Lütfen bu alt tür için bir birim seçin.");
            return;
        }

        // Ek güvenlik: bu kategori ile birim uyumlu mu?
        if (!BirimKurallari.birimUygunMu(kategori, birim)) {
            uyari("Seçilen birim bu kategori için geçerli değildir.");
            return;
        }

        try {
            // 1) Dinamik alt türü ekle (TXT + belleğe) — artık birim ile
            // --> AltTurHaritasi.yeniDinamikAltTur imzasını birim alacak şekilde güncelleyeceğiz
            DinamikAltTur altTur = AltTurHaritasi.yeniDinamikAltTur(kategori, ad, birim);

            // 2) Resmi kategori ekranının da kullandığı img klasörüne kopyala
            resmiProjeyeKopyala(altTur, secilenResim);

            uyari("Alt tür eklendi:\n\n" +
                    "Kategori: " + kategori.name().replace("_", " ") + "\n" +
                    "Ad: " + altTur.getAd() + "\n" +
                    "Birim: " + birim.name());

            // 3) Alanları temizle
            adField.clear();
            if (resimLabel != null) resimLabel.setText("Resim seçilmedi");
            if (onizlemeResim != null) onizlemeResim.setImage(null);
            secilenResim = null;
            if (birimBox != null) {
                birimBox.getSelectionModel().clearSelection();
            }

        } catch (IllegalArgumentException ex) {
            uyari("Hata: " + ex.getMessage());
        }
    }

    /**
     * Dinamik alt tür için resim dosyasını,
     * KategoriController'ın da baktığı /tr/com/cicekstok/img klasörüne kopyalar.
     * Böylece alt tür kartları açıldığında resim direkt görünür.
     */
    private void resmiProjeyeKopyala(DinamikAltTur altTur, File kaynak) {
        try {
            // 1) Kod: DinamikAltTur içindeki "kod" bilgisini kullan
            String kod = altTur.getKod().toLowerCase(Locale.ROOT);
            kod = kod
                    .replace("ç", "c").replace("Ç", "c")
                    .replace("ğ", "g").replace("Ğ", "g")
                    .replace("ı", "i").replace("I", "i")
                    .replace("İ", "i")
                    .replace("ö", "o").replace("Ö", "o")
                    .replace("ş", "s").replace("Ş", "s")
                    .replace("ü", "u").replace("Ü", "u");

            // 2) Runtime'da kullanılan img klasörünü bul
            URL klasorUrl = getClass().getResource("/tr/com/cicekstok/img");
            if (klasorUrl == null) {
                uyari("img klasörü bulunamadı: /tr/com/cicekstok/img");
                return;
            }
            Path hedefKlasor = Paths.get(klasorUrl.toURI());

            if (!Files.exists(hedefKlasor)) {
                Files.createDirectories(hedefKlasor);
            }

            // 3) Uzantıyı koru (.png/.jpg/.jpeg)
            String isim = kaynak.getName();
            String uzanti = "";
            int idx = isim.lastIndexOf('.');
            if (idx != -1) {
                uzanti = isim.substring(idx); // .png / .jpg / .jpeg
            } else {
                uzanti = ".png";
            }

            Path hedef = hedefKlasor.resolve(kod + uzanti);

            Files.copy(kaynak.toPath(), hedef, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Resim kopyalandı: " + hedef);

        } catch (Exception e) {
            e.printStackTrace();
            uyari("Resim kopyalanırken hata oluştu:\n" + e.getMessage());
        }
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
