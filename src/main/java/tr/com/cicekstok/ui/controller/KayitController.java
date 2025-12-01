package tr.com.cicekstok.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tr.com.cicekstok.UygulamaBaglami;
import tr.com.cicekstok.ui.util.SceneManager;

import java.util.Map;

public class KayitController {

    @FXML private TextField kullaniciField;
    @FXML private PasswordField sifreField;
    @FXML private PasswordField sifreTekrarField;

    @FXML
    public void kaydol() {
        String k = kullaniciField.getText() == null ? "" : kullaniciField.getText().trim();
        String s1 = sifreField.getText() == null ? "" : sifreField.getText().trim();
        String s2 = sifreTekrarField.getText() == null ? "" : sifreTekrarField.getText().trim();

        if (k.isEmpty() || s1.isEmpty() || s2.isEmpty()) {
            uyari("Kullanıcı adı ve şifre alanları boş bırakılamaz.");
            return;
        }

        if (!s1.equals(s2)) {
            uyari("Şifreler birbiriyle uyuşmuyor.");
            return;
        }

        if (k.length() < 3) {
            uyari("Kullanıcı adı en az 3 karakter olmalı.");
            return;
        }

        if (s1.length() < 4) {
            uyari("Şifre en az 4 karakter olmalı.");
            return;
        }

        Map<String, String> mevcut = UygulamaBaglami.kullaniciDeposu.tumKullanicilar();
        if (mevcut.containsKey(k)) {
            uyari("Bu kullanıcı adı zaten kayıtlı.");
            return;
        }

        try {
            UygulamaBaglami.kullaniciDeposu.kaydet(k, s1);
        } catch (Exception e) {
            uyari("Kayıt sırasında hata oluştu: " + e.getMessage());
            return;
        }

        bilgi("Kayıt başarılı! Şimdi giriş yapabilirsiniz.");

        // Kayıttan sonra giriş ekranına dön
        SceneManager.showScene(
                "/tr/com/cicekstok/ui/view/giris.fxml",
                "Çiçek Stok Yönetimi - Giriş"
        );
    }

    @FXML
    public void geriDon() {
        SceneManager.showScene(
                "/tr/com/cicekstok/ui/view/giris.fxml",
                "Çiçek Stok Yönetimi - Giriş"
        );
    }

    private void uyari(String mesaj) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(mesaj);
        a.showAndWait();
    }

    private void bilgi(String mesaj) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(mesaj);
        a.showAndWait();
    }
}
