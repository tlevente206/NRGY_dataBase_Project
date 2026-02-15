package ua.com.nrgy.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ua.com.nrgy.model.*;
import ua.com.nrgy.util.ExcelExporter;
import ua.com.nrgy.util.PDFExporter;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExportController {
    @FXML private ToggleButton pdfToggle, excelToggle;
    @FXML private Label oszlopStatusz, beallitasStatusz;
    @FXML private TableView<Tag> previewTable;

    private ObservableList<Tag> tagok;
    private List<String> kivalasztottOszlopok = new ArrayList<>();

    // Paraméterek
    private boolean pdfLandscape = true;
    private int pdfFontSize = 10;
    private String pdfMargin = "Normál";
    private boolean pdfPageNumbers = true;
    private boolean excelHighlightEFJ = true;
    private boolean excelAutoFilter = true;
    private String excelSheetName = "Nyilvántartás";

    private final String[] osszesOszlop = {"Név", "Nem", "Kor", "Szül. Idő", "Szül. Hely", "Utca", "Hsz", "Telefon", "EFJ", "Presbiter", "Megjegyzés"};

    @FXML
    public void initialize() {
        kivalasztottOszlopok.addAll(List.of(osszesOszlop));
        ToggleGroup group = new ToggleGroup();
        pdfToggle.setToggleGroup(group);
        excelToggle.setToggleGroup(group);

        pdfToggle.selectedProperty().addListener((obs, oldV, newV) -> updateLabels());
        updateLabels();
    }

    public void setAdatok(ObservableList<Tag> tagok, ObservableList<Presbiter> presbiterek) {
        this.tagok = tagok;
        refreshPreviewTable();
    }

    private void refreshPreviewTable() {
        if (tagok == null) return;
        previewTable.getColumns().clear();

        for (String oszlopNev : kivalasztottOszlopok) {
            TableColumn<Tag, String> col = new TableColumn<>(oszlopNev);
            col.setCellValueFactory(data -> new SimpleStringProperty(getValueByCol(data.getValue(), oszlopNev)));
            col.setPrefWidth(100);
            previewTable.getColumns().add(col);
        }

        previewTable.setItems(tagok.size() > 15 ? FXCollections.observableArrayList(tagok.subList(0, 15)) : tagok);
        previewTable.setStyle("-fx-font-size: " + Math.min(pdfFontSize, 12) + "px;");
    }

    @FXML
    private void handleOszlopValaszto() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Oszlopok válogatása");
        VBox vb = new VBox(8);
        vb.setPadding(new Insets(20));

        for (String osz : osszesOszlop) {
            CheckBox cb = new CheckBox(osz);
            cb.setSelected(kivalasztottOszlopok.contains(osz));
            cb.selectedProperty().addListener((obs, oldV, newV) -> {
                if (newV) { if(!kivalasztottOszlopok.contains(osz)) kivalasztottOszlopok.add(osz); }
                else kivalasztottOszlopok.remove(osz);
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
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        if (pdfToggle.isSelected()) {
            dialog.setTitle("PDF Beállítások");
            ComboBox<String> orient = new ComboBox<>(FXCollections.observableArrayList("Fekvő", "Álló"));
            orient.setValue(pdfLandscape ? "Fekvő" : "Álló");
            Spinner<Integer> fontSpin = new Spinner<>(6, 14, pdfFontSize);
            ComboBox<String> marg = new ComboBox<>(FXCollections.observableArrayList("Keskeny", "Normál", "Széles"));
            marg.setValue(pdfMargin);

            grid.add(new Label("Tájolás:"), 0, 0); grid.add(orient, 1, 0);
            grid.add(new Label("Betűméret:"), 0, 1); grid.add(fontSpin, 1, 1);
            grid.add(new Label("Margó:"), 0, 2); grid.add(marg, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.showAndWait();

            pdfLandscape = orient.getValue().equals("Fekvő");
            pdfFontSize = fontSpin.getValue();
            pdfMargin = marg.getValue();
        } else {
            dialog.setTitle("Excel Beállítások");
            CheckBox filter = new CheckBox("Auto-szűrő"); filter.setSelected(excelAutoFilter);
            CheckBox color = new CheckBox("EFJ Piros kiemelés"); color.setSelected(excelHighlightEFJ);
            TextField sheet = new TextField(excelSheetName);

            grid.add(filter, 0, 0); grid.add(color, 0, 1);
            grid.add(new Label("Lap neve:"), 0, 2); grid.add(sheet, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.showAndWait();

            excelAutoFilter = filter.isSelected();
            excelHighlightEFJ = color.isSelected();
            excelSheetName = sheet.getText();
        }
        updateLabels();
    }

    private void updateLabels() {
        oszlopStatusz.setText(kivalasztottOszlopok.size() + " oszlop");
        beallitasStatusz.setText(pdfToggle.isSelected() ? "PDF: " + (pdfLandscape ? "Fekvő" : "Álló") : "Excel: " + excelSheetName);
        refreshPreviewTable();
    }

    @FXML
    private void handleExport() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Fájl mentése");
        fc.setInitialFileName("Export_" + LocalDate.now());

        File file;
        if (pdfToggle.isSelected()) {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF fájl", "*.pdf"));
            file = fc.showSaveDialog(pdfToggle.getScene().getWindow());
            if (file != null) {
                PDFExporter.exportTagok(tagok, file.getAbsolutePath(), kivalasztottOszlopok,
                        pdfLandscape, pdfFontSize, pdfMargin, pdfPageNumbers);
                finishExport();
            }
        } else {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel fájl", "*.xlsx"));
            file = fc.showSaveDialog(excelToggle.getScene().getWindow());
            if (file != null) {
                ExcelExporter.exportTagok(tagok, file.getAbsolutePath(), kivalasztottOszlopok,
                        excelHighlightEFJ, excelAutoFilter, excelSheetName);
                finishExport();
            }
        }
    }

    private void finishExport() {
        // Kis késleltetés után (opcionális) bezárjuk az ablakot
        Stage stage = (Stage) pdfToggle.getScene().getWindow();
        stage.close();

        // Visszajelzés a felhasználónak
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sikeres mentés");
        alert.setHeaderText(null);
        alert.setContentText("A dokumentum elkészült!");
        alert.show();
    }

    private String getValueByCol(Tag t, String col) {
        return switch(col) {
            case "Név" -> t.getNev();
            case "Kor" -> String.valueOf(t.getEletkor());
            case "EFJ" -> t.isEfj_befizetes() ? "Igen" : "Nem";
            case "Telefon" -> t.getTelefonszam();
            case "Utca" -> t.getUtcaNeve();
            case "Hsz" -> t.getHazszam();
            case "Nem" -> t.getNem();
            case "Szül. Idő" -> t.getSzul_ido();
            case "Szül. Hely" -> t.getSzul_hely();
            case "Presbiter" -> t.getPresbiterNeve();
            case "Megjegyzés" -> t.getMegjegyzes();
            default -> "";
        };
    }
}