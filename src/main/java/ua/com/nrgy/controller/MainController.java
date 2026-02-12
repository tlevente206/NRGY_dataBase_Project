package ua.com.nrgy.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ua.com.nrgy.model.DatabaseHandler;
import ua.com.nrgy.model.Tag;

public class MainController {

    @FXML
    private TableView<Tag> tagokTablazat;

    @FXML
    private void handleUjTag() {
        // 1. Létrehozunk egy próbát
        Tag ujTag = new Tag(0, "Próba Péter", "1990.05.05", "Beregszász",
                "Fő út 10.", "06209998877", true, 1);

        // 2. Elmentjük
        DatabaseHandler.addTag(ujTag);

        // 3. Kiíratjuk az ÖSSZES eddigi tagot az adatbázisból
        DatabaseHandler.getAllTags();
    }
}