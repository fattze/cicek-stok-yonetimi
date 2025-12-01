package tr.com.cicekstok;

import javafx.application.Application;
import javafx.stage.Stage;
import tr.com.cicekstok.ui.util.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Stage'i SceneManager'a veriyoruz
        SceneManager.setStage(stage);

        // İlk açılış ekranı: Giriş ekranı
        SceneManager.showScene(
                "/tr/com/cicekstok/ui/view/giris.fxml",
                "Çiçek Stok Yönetimi - Giriş"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
