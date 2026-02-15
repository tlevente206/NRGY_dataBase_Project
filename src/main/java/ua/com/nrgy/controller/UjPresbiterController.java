package ua.com.nrgy.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ua.com.nrgy.model.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UjPresbiterController {

    @FXML private Label ablakCim;
    @FXML private TextField nevField, szulHelyField, hazszamField, telField, beiktatasField;
    @FXML private TextArea megjegyzesArea;
    @FXML private DatePicker szulIdoPicker;
    @FXML private CheckBox efjCheckBox;
    @FXML private ComboBox<String> nemComboBox;
    @FXML private ComboBox<Utca> utcaComboBox;

    private final PresbiterDAO presbiterDAO = new PresbiterDAO();
    private final UtcaDAO utcaDAO = new UtcaDAO();
    private Runnable onSaveCallback;
    private Presbiter modositandoPresbiter;

    @FXML
    public void initialize() {
        nemComboBox.getItems().addAll("Férfi", "Nő");

        utcaComboBox.getItems().setAll(utcaDAO.findAll());
        utcaComboBox.setConverter(new StringConverter<Utca>() {
            @Override public String toString(Utca u) { return (u == null) ? "" : u.getNev(); }
            @Override public Utca fromString(String s) { return null; }
        });
    }

    public void setOnSaveCallback(Runnable callback) { this.onSaveCallback = callback; }

    public void setPresbiterAdatok(Presbiter p) {
        this.modositandoPresbiter = p;
        ablakCim.setText("Presbiter módosítása");
        nevField.setText(p.getNev());
        nemComboBox.setValue(p.getNem());
        szulHelyField.setText(p.getSzul_hely());
        hazszamField.setText(p.getHazszam());
        telField.setText(p.getTelefonszam());
        beiktatasField.setText(String.valueOf(p.getBeiktatas_eve()));
        efjCheckBox.setSelected(p.isEfj_befizetes());
        megjegyzesArea.setText(p.getMegjegyzes());

        if (p.getSzul_ido() != null && !p.getSzul_ido().isEmpty()) {
            szulIdoPicker.setValue(LocalDate.parse(p.getSzul_ido(), DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        }

        utcaComboBox.getItems().stream()
                .filter(u -> u.getId() == p.getUtca_id())
                .findFirst().ifPresent(u -> utcaComboBox.setValue(u));
    }

    @FXML
    private void handleMentes() {
        if (nevField.getText().isEmpty()) { return; }

        String szulIdo = (szulIdoPicker.getValue() != null)
                ? szulIdoPicker.getValue().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "";

        int utcaId = (utcaComboBox.getValue() != null) ? utcaComboBox.getValue().getId() : 0;
        int beiktatas = 0;
        try { beiktatas = Integer.parseInt(beiktatasField.getText()); } catch (NumberFormatException e) { /* marad 0 */ }

        Presbiter p = new Presbiter(
                (modositandoPresbiter != null) ? modositandoPresbiter.getId() : 0,
                nevField.getText(),
                nemComboBox.getValue(),
                szulIdo,
                szulHelyField.getText(),
                utcaId,
                hazszamField.getText(),
                telField.getText(),
                efjCheckBox.isSelected(),
                beiktatas,
                megjegyzesArea.getText()
        );

        if (modositandoPresbiter == null) presbiterDAO.save(p);
        else presbiterDAO.update(p);

        if (onSaveCallback != null) onSaveCallback.run();
        closeWindow();
    }

    @FXML private void handleMegse() { closeWindow(); }
    private void closeWindow() { ((Stage) nevField.getScene().getWindow()).close(); }
}