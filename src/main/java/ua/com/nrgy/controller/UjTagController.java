package ua.com.nrgy.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ua.com.nrgy.model.Presbiter;
import ua.com.nrgy.model.PresbiterDAO;
import ua.com.nrgy.model.Tag;
import ua.com.nrgy.model.TagDAO;
import java.time.format.DateTimeFormatter;

public class UjTagController {

    @FXML private TextField nevField, szulHelyField, lakcimField, telField;
    @FXML private DatePicker szulIdoPicker;
    @FXML private CheckBox efjCheckBox;
    @FXML private ComboBox<Presbiter> presbiterComboBox;

    private TagDAO tagDAO = new TagDAO();
    private PresbiterDAO presbiterDAO = new PresbiterDAO();
    private Runnable onSaveCallback;

    @FXML
    public void initialize() {
        // 1. Betöltjük a presbitereket az adatbázisból
        presbiterComboBox.getItems().setAll(presbiterDAO.findAll());

        // 2. Beállítjuk a StringConverter-t, hogy a nevet jelenítse meg
        presbiterComboBox.setConverter(new StringConverter<Presbiter>() {
            @Override
            public String toString(Presbiter p) {
                return (p == null) ? "" : p.getNev();
            }

            @Override
            public Presbiter fromString(String string) {
                return null; // Nem szükséges a visszafele alakítás
            }
        });
    }

    // Ezt a metódust hívja meg a MainController, mielőtt megnyitná az ablakot
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleMentes() {
        if (nevField.getText() == null || nevField.getText().trim().isEmpty()) {
            alert("Hiba", "A név megadása kötelező!");
            return;
        }

        // Dátum formázása: 2026.02.13
        String szulIdo = (szulIdoPicker.getValue() != null)
                ? szulIdoPicker.getValue().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                : "";

        // Kiválasztott presbiter ID lekérése
        Presbiter valasztottPresbiter = presbiterComboBox.getValue();
        int presId = (valasztottPresbiter != null) ? valasztottPresbiter.getId() : 0;

        Tag ujTag = new Tag(
                0, // Az ID automatikusan generálódik az adatbázisban
                nevField.getText(),
                szulIdo,
                szulHelyField.getText(),
                lakcimField.getText(),
                telField.getText(),
                efjCheckBox.isSelected(),
                presId
        );

        // Mentés az adatbázisba
        tagDAO.save(ujTag);

        // Főablak táblázatának frissítése
        if (onSaveCallback != null) {
            onSaveCallback.run();
        }

        closeWindow();
    }

    @FXML
    private void handleMegse() {
        closeWindow();
    }

    private void closeWindow() {
        if (nevField.getScene() != null) {
            ((Stage) nevField.getScene().getWindow()).close();
        }
    }

    private void alert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}