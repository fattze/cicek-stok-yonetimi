package tr.com.cicekstok.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tr.com.cicekstok.UygulamaBaglami;
import tr.com.cicekstok.ui.util.SceneManager;

import java.util.Map;

public class GirisController {

    @FXML
    private TextField kullaniciField;

    @FXML
    private PasswordField sifreField;

    @FXML
    public void girisYap() {
        String k = kullaniciField.getText() == null ? "" : kullaniciField.getText().trim();
        String s = sifreField.getText() == null ? "" : sifreField.getText().trim();

        if (k.isEmpty() || s.isEmpty()) {
            uyari("Kullanıcı adı ve şifre boş olamaz.");
            return;
        }

        Map<String, String> kullanicilar =
                UygulamaBaglami.kullaniciDeposu.tumKullanicilar();

        String kayitliSifre = kullanicilar.get(k);

        // Kullanıcı yoksa veya şifre yanlışsa
        if (kayitliSifre == null || !kayitliSifre.equals(s)) {
            uyari("Kullanıcı adı veya şifre hatalı.");
            return;
        }

        // Giriş başarılı → aktif kullanıcıyı kaydet
        UygulamaBaglami.aktifKullanici = k;

        // Ana ekrana geç
        SceneManager.showScene(
                "/tr/com/cicekstok/ui/view/ana.fxml",
                "Çiçek Stok Yönetimi"
        );
    }

    /**
     * "Kayıt Ol" butonuna bağlı metot
     */
    @FXML
    public void kayitEkraniAc() {
        SceneManager.showScene(
                "/tr/com/cicekstok/ui/view/kayit.fxml",
                "Yeni Kullanıcı Kaydı"
        );
    }

    private void uyari(String mesaj) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(mesaj);
        a.showAndWait();
    }
}
