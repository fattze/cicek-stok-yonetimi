package tr.com.cicekstok.ui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;

    // Main'den sadece 1 kere çağrılıyor
    public static void setStage(Stage stage) {
        primaryStage = stage;

        // 🔹 Uygulama boyunca kullanılacak pencere ayarları
        primaryStage.setMaximized(true);   // her zaman büyük aç
        primaryStage.setMinWidth(1200);    // daha küçük olmasın
        primaryStage.setMinHeight(700);
    }

    // Hangi FXML açılırsa açılsın, hep aynı Stage'e yüklenir
    public static void showScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            if (primaryStage.getScene() == null) {
                // İlk defa sahne oluşturuluyor
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                // Sahne zaten var, sadece root'u değiştir
                primaryStage.getScene().setRoot(root);
            }

            primaryStage.setTitle(title);

            // Pencere her zaman büyük kalsın
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(700);
            primaryStage.setMaximized(true);

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("FXML yüklenemedi: " + fxmlPath);
        }
    }
}
