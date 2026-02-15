package ua.com.nrgy.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ua.com.nrgy.model.*;
import ua.com.nrgy.util.PDFExporter;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExportController {
    @FXML private Label oszlopStatusz, beallitasStatusz;
    @FXML private ToggleButton pdfToggle; // Az FXML-ből jön a választás

    // Adatok tárolása
    private ObservableList<Tag> tagok;
    private ObservableList<Presbiter> presbiterek;

    // Beállítások tárolása
    private List<String> kivalasztottOszlopok = new ArrayList<>();
    private boolean isLandscape = true;
    private int betumeret = 10;

    private final String[] osszesOszlop = {"Név", "Nem", "Kor", "Szül. Idő", "Szül. Hely", "Utca", "Hsz", "Telefon", "EFJ", "Presbiter", "Megjegyzés"};

    @FXML
    public void initialize() {
        kivalasztottOszlopok.addAll(List.of(osszesOszlop));
        updateLabels();
    }

    // EZT HÍVJA A MAINCONTROLLER
    public void setAdatok(ObservableList<Tag> tagok, ObservableList<Presbiter> presbiterek) {
        this.tagok = tagok;
        this.presbiterek = presbiterek;
    }

    @FXML
    private void handleOszlopValaszto() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Oszlopok");
        VBox vb = new VBox(10);
        vb.setPadding(new javafx.geometry.Insets(20));

        for (String osz : osszesOszlop) {
            CheckBox cb = new CheckBox(osz);
            cb.setSelected(kivalasztottOszlopok.contains(osz));
            cb.selectedProperty().addListener((obs, oldV, newV) -> {
                if (newV) {
                    if (!kivalasztottOszlopok.contains(osz)) kivalasztottOszlopok.add(osz);
                } else {
                    kivalasztottOszlopok.remove(osz);
                }
                updateLabels();
            });
            vb.getChildren().add(cb);
        }

        dialog.getDialogPane().setContent(vb);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    private void handleOldalBeallitasok() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Oldalbeállítások");
        VBox vb = new VBox(15);
        vb.setPadding(new javafx.geometry.Insets(20));

        ToggleGroup tajolasGroup = new ToggleGroup();
        RadioButton rb1 = new RadioButton("Fekvő (Landscape)");
        rb1.setToggleGroup(tajolasGroup);
        rb1.setSelected(isLandscape);

        RadioButton rb2 = new RadioButton("Álló (Portrait)");
        rb2.setToggleGroup(tajolasGroup);
        rb2.setSelected(!isLandscape);

        tajolasGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> {
            isLandscape = (newV == rb1);
            updateLabels();
        });

        ComboBox<Integer> fontCombo = new ComboBox<>(FXCollections.observableArrayList(8, 10, 12));
        fontCombo.setValue(betumeret);
        fontCombo.setOnAction(e -> betumeret = fontCombo.getValue());

        vb.getChildren().addAll(new Label("Tájolás:"), rb1, rb2, new Label("Betűméret:"), fontCombo);
        dialog.getDialogPane().setContent(vb);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void updateLabels() {
        oszlopStatusz.setText(kivalasztottOszlopok.size() + " oszlop kijelölve");
        beallitasStatusz.setText((isLandscape ? "Fekvő" : "Álló") + ", " + betumeret + "pt betű");
    }

    @FXML
    private void handleExport() {
        if (kivalasztottOszlopok.isEmpty()) {
            alert("Hiba", "Válasszon ki legalább egy oszlopot!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Mentés");
        fileChooser.setInitialFileName("Export_" + LocalDate.now());

        if (pdfToggle.isSelected()) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF fájl", "*.pdf"));
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel fájl", "*.xlsx"));
        }

        File file = fileChooser.showSaveDialog(oszlopStatusz.getScene().getWindow());

        if (file != null) {
            if (pdfToggle.isSelected()) {
                // PDF mentés indítása
                PDFExporter.exportTagok(tagok, file.getAbsolutePath(), kivalasztottOszlopok, isLandscape, betumeret);
            } else {
                // Itt fogjuk hívni az ExcelExporter-t
                System.out.println("Excel mentés folyamatban: " + file.getAbsolutePath());
            }
            // Ablak bezárása mentés után
            ((Stage) oszlopStatusz.getScene().getWindow()).close();
        }
    }

    private void alert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}