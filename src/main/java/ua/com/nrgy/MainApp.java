package ua.com.nrgy;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.com.nrgy.model.DatabaseHandler;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 1. ADATBÁZIS INICIALIZÁLÁSA
        // Ez létrehozza a .db fájlt és a táblákat még a UI betöltése előtt
        DatabaseHandler.initialize();

        // 2. DESIGN BEÁLLÍTÁSA
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // 3. UI BETÖLTÉSE
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("main_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.setTitle("Református Gyülekezeti Nyilvántartó");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}