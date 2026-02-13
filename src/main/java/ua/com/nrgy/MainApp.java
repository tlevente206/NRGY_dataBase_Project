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
        DatabaseHandler.initialize();

        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("main_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load()); // Itt már nem kell fix méret (800, 600)

        stage.setTitle("Református Gyülekezeti Nyilvántartó");
        stage.setScene(scene);

        // EZ A SOR KELL: Maximalizálva indul, de a gombokkal kicsinyíthető marad
        stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}