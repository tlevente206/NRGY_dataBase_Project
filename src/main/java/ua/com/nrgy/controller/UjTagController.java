package ua.com.nrgy.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ua.com.nrgy.model.*;
import java.time.format.DateTimeFormatter;

public class UjTagController {

    @FXML private TextField nevField, szulHelyField, hazszamField, telField;
    @FXML private TextArea megjegyzesArea;
    @FXML private DatePicker szulIdoPicker;
    @FXML private CheckBox efjCheckBox;
    @FXML private ComboBox<String> nemComboBox;
    @FXML private ComboBox<Utca> utcaComboBox;
    @FXML private ComboBox<Presbiter> presbiterComboBox;

    private final TagDAO tagDAO = new TagDAO();
    private final PresbiterDAO presbiterDAO = new PresbiterDAO();
    private final UtcaDAO utcaDAO = new UtcaDAO();
    private Runnable onSaveCallback;

    @FXML
    public void initialize() {
        // 1. Nemek beállítása
        nemComboBox.getItems().addAll("Férfi", "Nő");

        // 2. Utcák betöltése
        utcaComboBox.getItems().setAll(utcaDAO.findAll());
        utcaComboBox.setConverter(new StringConverter<Utca>() {
            @Override public String toString(Utca u) { return (u == null) ? "" : u.getNev(); }
            @Override public Utca fromString(String s) { return null; }
        });

        // 3. Presbiterek betöltése
        presbiterComboBox.getItems().setAll(presbiterDAO.findAll());
        presbiterComboBox.setConverter(new StringConverter<Presbiter>() {
            @Override public String toString(Presbiter p) { return (p == null) ? "" : p.getNev(); }
            @Override public Presbiter fromString(String s) { return null; }
        });
    }

    public void setOnSaveCallback(Runnable callback) { this.onSaveCallback = callback; }

    @FXML
    private void handleMentes() {
        if (nevField.getText() == null || nevField.getText().trim().isEmpty()) {
            alert("Hiba", "A név megadása kötelező!");
            return;
        }

        String szulIdo = (szulIdoPicker.getValue() != null)
                ? szulIdoPicker.getValue().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                : "";

        // ID-k lekérése a ComboBoxokból
        int utcaId = (utcaComboBox.getValue() != null) ? utcaComboBox.getValue().getId() : 0;
        int presId = (presbiterComboBox.getValue() != null) ? presbiterComboBox.getValue().getId() : 0;
        String nem = (nemComboBox.getValue() != null) ? nemComboBox.getValue() : "Nincs megadva";

        // Itt hívjuk meg az ÚJ konstruktort (11 paraméter!)
        Tag ujTag = new Tag(
                0,
                nevField.getText(),
                nem,
                szulIdo,
                szulHelyField.getText(),
                utcaId,
                hazszamField.getText(),
                telField.getText(),
                efjCheckBox.isSelected(),
                presId,
                megjegyzesArea.getText()
        );

        tagDAO.save(ujTag);
        if (onSaveCallback != null) onSaveCallback.run();
        closeWindow();
    }

    @FXML private void handleMegse() { closeWindow(); }

    private void closeWindow() {
        ((Stage) nevField.getScene().getWindow()).close();
    }

    private void alert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}