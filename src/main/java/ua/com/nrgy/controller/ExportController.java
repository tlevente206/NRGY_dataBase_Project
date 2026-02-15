package ua.com.nrgy.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ua.com.nrgy.model.*;
import ua.com.nrgy.util.ExcelExporter;
import ua.com.nrgy.util.PDFExporter;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExportController {
    @FXML private ToggleButton pdfToggle, excelToggle;
    @FXML private Label oszlopStatusz, beallitasStatusz;
    @FXML private VBox paperSheet;

    private ObservableList<Tag> tagok;
    private List<String> kivalasztottOszlopok = new ArrayList<>();

    private boolean isLandscape = true;
    private int betumeret = 9;
    private String margo = "Normál";
    private boolean oldalszam = true;
    private String excelLapNev = "Nyilvántartás";

    private final String[] osszesOszlop = {"Név", "Nem", "Kor", "Szül. Idő", "Szül. Hely", "Utca", "Hsz", "Telefon", "EFJ", "Presbiter", "Megjegyzés"};

    @FXML
    public void initialize() {
        kivalasztottOszlopok.addAll(List.of(osszesOszlop));
        ToggleGroup tg = new ToggleGroup();
        pdfToggle.setToggleGroup(tg); excelToggle.setToggleGroup(tg);
        pdfToggle.selectedProperty().addListener((obs, o, n) -> updatePreview());
        updatePreview();
    }

    public void setAdatok(ObservableList<Tag> tagok, ObservableList<Presbiter> presbiterek) {
        this.tagok = tagok;
        updatePreview();
    }

    private void updatePreview() {
        if (tagok == null) return;

        oszlopStatusz.setText(kivalasztottOszlopok.size() + " oszlop kiválasztva");
        beallitasStatusz.setText(pdfToggle.isSelected() ? "PDF: " + (isLandscape ? "Fekvő" : "Álló") : "Excel: " + excelLapNev);

        paperSheet.getChildren().clear();
        paperSheet.setPrefWidth(isLandscape ? 842 : 595);
        paperSheet.setMinHeight(isLandscape ? 595 : 842);

        // Margó szimuláció (PDF arányokhoz igazítva)
        double p = margo.equals("Keskeny") ? 15 : (margo.equals("Széles") ? 60 : 35);
        paperSheet.setPadding(new Insets(p));

        // 1. PDF Fejléc szimuláció (Dátum és Cím)
        Label dateLabel = new Label("Készült: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
        dateLabel.setStyle("-fx-font-size: 8pt; -fx-font-family: 'Arial';");

        Label titleLabel = new Label("Gyülekezeti Nyilvántartás");
        titleLabel.setStyle("-fx-font-size: " + (betumeret + 6) + "pt; -fx-font-weight: bold; -fx-font-family: 'Arial';");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);

        paperSheet.getChildren().addAll(dateLabel, titleLabel, new Region() {{ setMinHeight(15); }});

        // 2. Táblázat szimuláció
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);
        grid.setStyle("-fx-border-color: black; -fx-border-width: 0.5;");

        // Fejlécek (szürke háttérrel)
        for (int i = 0; i < kivalasztottOszlopok.size(); i++) {
            Label h = new Label(kivalasztottOszlopok.get(i));
            h.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-padding: 3; -fx-font-size: " + betumeret + "pt;");
            h.setMaxWidth(Double.MAX_VALUE);
            grid.add(h, i, 0);
        }

        // Adat mintavétel (A PDF-ben látott konkrét rekordokhoz hasonlóan)
        int limit = Math.min(tagok.size(), 15);
        for (int row = 0; row < limit; row++) {
            for (int col = 0; col < kivalasztottOszlopok.size(); col++) {
                Label cell = new Label(getValue(tagok.get(row), kivalasztottOszlopok.get(col)));
                cell.setStyle("-fx-padding: 2; -fx-font-size: " + betumeret + "pt; -fx-font-family: 'Arial';");
                grid.add(cell, col, row + 1);
            }
        }
        paperSheet.getChildren().add(grid);

        // 3. Oldalszám az alján
        if (oldalszam) {
            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);
            Label pNum = new Label("1. oldal");
            pNum.setStyle("-fx-font-size: 9pt; -fx-font-family: 'Arial';");
            paperSheet.getChildren().addAll(spacer, pNum);
        }
    }

    @FXML
    private void handleOldalBeallitasok() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Beállítások");
        VBox vb = new VBox(10);
        vb.setPadding(new Insets(20));

        if (pdfToggle.isSelected()) {
            CheckBox cbL = new CheckBox("Fekvő tájolás"); cbL.setSelected(isLandscape);
            ComboBox<String> margoCombo = new ComboBox<>(FXCollections.observableArrayList("Keskeny", "Normál", "Széles"));
            margoCombo.setValue(margo);
            vb.getChildren().addAll(new Label("Tájolás:"), cbL, new Label("Margó:"), margoCombo);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().setContent(vb);
            dialog.showAndWait();
            isLandscape = cbL.isSelected();
            margo = margoCombo.getValue();
        } else {
            TextField tf = new TextField(excelLapNev);
            vb.getChildren().addAll(new Label("Munkalap neve:"), tf);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().setContent(vb);
            dialog.showAndWait();
            excelLapNev = tf.getText();
        }
        updatePreview();
    }

    @FXML
    private void handleOszlopValaszto() {
        Dialog<Void> dialog = new Dialog<>();
        VBox vb = new VBox(5);
        vb.setPadding(new Insets(15));
        for (String s : osszesOszlop) {
            CheckBox cb = new CheckBox(s); cb.setSelected(kivalasztottOszlopok.contains(s));
            cb.selectedProperty().addListener((o, old, n) -> {
                if (n) { if(!kivalasztottOszlopok.contains(s)) kivalasztottOszlopok.add(s); }
                else kivalasztottOszlopok.remove(s);
                updatePreview();
            });
            vb.getChildren().add(cb);
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setContent(vb);
        dialog.showAndWait();
    }

    @FXML
    private void handleExport() {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("Export_" + LocalDate.now());
        if (pdfToggle.isSelected()) {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            File f = fc.showSaveDialog(paperSheet.getScene().getWindow());
            if (f != null) {
                PDFExporter.exportTagok(tagok, f.getAbsolutePath(), kivalasztottOszlopok, isLandscape, betumeret, margo, oldalszam);
                ((Stage) paperSheet.getScene().getWindow()).close();
            }
        } else {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
            File f = fc.showSaveDialog(paperSheet.getScene().getWindow());
            if (f != null) {
                ExcelExporter.exportTagok(tagok, f.getAbsolutePath(), kivalasztottOszlopok, true, true, excelLapNev);
                ((Stage) paperSheet.getScene().getWindow()).close();
            }
        }
    }

    private String getValue(Tag t, String col) {
        if (t == null) return "";
        return switch(col) {
            case "Név" -> t.getNev() != null ? t.getNev() : "";
            case "Nem" -> t.getNem() != null ? t.getNem() : "";
            case "Kor" -> String.valueOf(t.getEletkor());
            case "Szül. Idő" -> t.getSzul_ido() != null ? t.getSzul_ido() : "";
            case "Szül. Hely" -> t.getSzul_hely() != null ? t.getSzul_hely() : "";
            case "Utca" -> t.getUtcaNeve() != null ? t.getUtcaNeve() : "";
            case "Hsz" -> t.getHazszam() != null ? t.getHazszam() : "";
            case "Telefon" -> t.getTelefonszam() != null ? t.getTelefonszam() : "";
            case "EFJ" -> t.isEfj_befizetes() ? "Igen" : "Nem";
            case "Presbiter" -> t.getPresbiterNeve() != null ? t.getPresbiterNeve() : "";
            case "Megjegyzés" -> t.getMegjegyzes() != null ? t.getMegjegyzes() : "";
            default -> "";
        };
    }
}