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

        int utcaId = (utcaComboBox.getValue() != null) ? utcaComboBox.getValue().getId() : 0;
        int presId = (presbiterComboBox.getValue() != null) ? presbiterComboBox.getValue().getId() : 0;
        String nemValasztva = (nemComboBox.getValue() != null) ? nemComboBox.getValue() : "Nincs megadva";

        // 11 paraméteres konstruktor hívása
        Tag tag = new Tag(
                (modositandoTag != null) ? modositandoTag.getId() : 0,
                nevField.getText(),
                nemValasztva,
                szulIdo,
                szulHelyField.getText(),
                utcaId,
                hazszamField.getText(),
                telField.getText(),
                efjCheckBox.isSelected(),
                presId,
                megjegyzesArea.getText()
        );

        if (modositandoTag == null) {
            tagDAO.save(tag);
        } else {
            tagDAO.update(tag);
        }

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

    private Tag modositandoTag; // Ha null, akkor ÚJ, ha nem null, akkor MÓDOSÍTÁS

    // Ezt a metódust hívjuk meg a MainControllerből dupla kattintáskor
    public void setTagAdatok(Tag tag) {
        this.modositandoTag = tag;

        // Mezők feltöltése a meglévő adatokkal
        nevField.setText(tag.getNev());
        nemComboBox.setValue(tag.getNem());
        szulHelyField.setText(tag.getSzul_hely());
        hazszamField.setText(tag.getHazszam());
        telField.setText(tag.getTelefonszam());
        efjCheckBox.setSelected(tag.isEfj_befizetes());
        megjegyzesArea.setText(tag.getMegjegyzes());

        // Dátum visszaállítása
        if (tag.getSzul_ido() != null && !tag.getSzul_ido().isEmpty()) {
            szulIdoPicker.setValue(java.time.LocalDate.parse(tag.getSzul_ido(), java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        }

        // ComboBoxok beállítása (ID alapján megkeressük az objektumot a listában)
        utcaComboBox.getItems().stream().filter(u -> u.getId() == tag.getUtca_id()).findFirst().ifPresent(u -> utcaComboBox.setValue(u));
        presbiterComboBox.getItems().stream().filter(p -> p.getId() == tag.getPresbiter_id()).findFirst().ifPresent(p -> presbiterComboBox.setValue(p));
    }


}