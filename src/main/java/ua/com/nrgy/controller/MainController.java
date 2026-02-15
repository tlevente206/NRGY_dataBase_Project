package ua.com.nrgy.controller;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ua.com.nrgy.model.*;

import java.io.IOException;

public class MainController {

    // --- Táblázatok ---
    @FXML private TableView<Tag> tagokTablazat;
    @FXML private TableView<Presbiter> presbiterTablazat;

    // --- Oszlopok (Tagok) ---
    @FXML private TableColumn<Tag, String> tagNevOszlop;
    @FXML private TableColumn<Tag, String> tagNemOszlop;
    @FXML private TableColumn<Tag, Integer> tagEletkorOszlop; // Számított kor
    @FXML private TableColumn<Tag, String> tagSzulIdoOszlop;
    @FXML private TableColumn<Tag, String> tagSzulHelyOszlop;
    @FXML private TableColumn<Tag, String> tagUtcaOszlop;
    @FXML private TableColumn<Tag, String> tagHazszamOszlop;
    @FXML private TableColumn<Tag, String> tagTelOszlop;
    @FXML private TableColumn<Tag, Boolean> tagEfjOszlop;
    @FXML private TableColumn<Tag, String> tagPresbitereOszlop;
    @FXML private TableColumn<Tag, String> tagMegjegyzesOszlop;

    // --- Oszlopok (Presbiterek) ---
    @FXML private TableColumn<Presbiter, String> presNevOszlop;
    @FXML private TableColumn<Presbiter, String> presNemOszlop;
    @FXML private TableColumn<Presbiter, Integer> presEletkorOszlop; // Számított kor
    @FXML private TableColumn<Presbiter, String> presSzulIdoOszlop;
    @FXML private TableColumn<Presbiter, String> presSzulHelyOszlop;
    @FXML private TableColumn<Presbiter, String> presUtcaOszlop;
    @FXML private TableColumn<Presbiter, String> presHazszamOszlop;
    @FXML private TableColumn<Presbiter, String> presTelOszlop;
    @FXML private TableColumn<Presbiter, Boolean> presEfjOszlop;
    @FXML private TableColumn<Presbiter, Integer> presBeiktatasOszlop;
    @FXML private TableColumn<Presbiter, String> presMegjegyzesOszlop;

    // --- Keresés elemei ---
    @FXML private ComboBox<String> tagKeresoOszlopCombo;
    @FXML private TextField tagKeresoField;
    @FXML private ComboBox<String> presKeresoOszlopCombo;
    @FXML private TextField presKeresoField;

    @FXML private Label tagSorSzamlalo;
    @FXML private Label presSorSzamlalo;

    private final TagDAO tagDAO = new TagDAO();
    private final PresbiterDAO presbiterDAO = new PresbiterDAO();

    private final ObservableList<Tag> tagokLista = FXCollections.observableArrayList();
    private final ObservableList<Presbiter> presbiterekLista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();

        // Táblázat vizuális finomítása
        tagokTablazat.setFixedCellSize(38);
        presbiterTablazat.setFixedCellSize(38);

        // Kereső opciók (benne az életkor és születési idő is)
        tagKeresoOszlopCombo.getItems().setAll(
                "Név", "Nem", "Kor", "Szül. Idő", "Szül. Hely", "Utca", "Hsz", "Telefon", "EFJ", "Presbitere", "Megjegyzés"
        );
        tagKeresoOszlopCombo.getSelectionModel().selectFirst();

        // Presbiterek kereső combo
        presKeresoOszlopCombo.getItems().setAll(
                "Név", "Nem", "Kor", "Szül. Idő", "Szül. Hely", "Utca", "Hsz", "Telefon", "EFJ", "Beiktatva", "Megjegyzés"
        );
        presKeresoOszlopCombo.getSelectionModel().selectFirst();

        // Eseménykezelők és adatok betöltése
        setupKereso();
        setupPresbiterKereso();
        setupTableEvents();
        frissitTablakat();
    }

    private void setupTableColumns() {
        // Tagok oszlopainak összekötése a modell Property-jeivel
        tagNevOszlop.setCellValueFactory(c -> c.getValue().nevProperty());
        tagNemOszlop.setCellValueFactory(c -> c.getValue().nemProperty());
        tagEletkorOszlop.setCellValueFactory(c -> c.getValue().eletkorProperty().asObject());
        tagSzulIdoOszlop.setCellValueFactory(c -> c.getValue().szul_idoProperty());
        tagSzulHelyOszlop.setCellValueFactory(c -> c.getValue().szul_helyProperty());
        tagUtcaOszlop.setCellValueFactory(c -> c.getValue().utcaNeveProperty());
        tagHazszamOszlop.setCellValueFactory(c -> c.getValue().hazszamProperty());
        tagTelOszlop.setCellValueFactory(c -> c.getValue().telefonszamProperty());
        tagEfjOszlop.setCellValueFactory(c -> c.getValue().efj_befizetesProperty().asObject());
        tagPresbitereOszlop.setCellValueFactory(c -> c.getValue().presbiterNeveProperty());
        tagMegjegyzesOszlop.setCellValueFactory(c -> c.getValue().megjegyzesProperty());

        // Presbiterek oszlopainak összekötése
        presNevOszlop.setCellValueFactory(c -> c.getValue().nevProperty());
        presNemOszlop.setCellValueFactory(c -> c.getValue().nemProperty());
        presEletkorOszlop.setCellValueFactory(c -> c.getValue().eletkorProperty().asObject());
        presSzulIdoOszlop.setCellValueFactory(c -> c.getValue().szul_idoProperty());
        presSzulHelyOszlop.setCellValueFactory(c -> c.getValue().szul_helyProperty());
        presUtcaOszlop.setCellValueFactory(c -> c.getValue().utcaNeveProperty());
        presHazszamOszlop.setCellValueFactory(c -> c.getValue().hazszamProperty());
        presTelOszlop.setCellValueFactory(c -> c.getValue().telefonszamProperty());
        presEfjOszlop.setCellValueFactory(c -> c.getValue().efj_befizetesProperty().asObject());
        presBeiktatasOszlop.setCellValueFactory(c -> c.getValue().beiktatas_eveProperty().asObject());
        presMegjegyzesOszlop.setCellValueFactory(c -> c.getValue().megjegyzesProperty());
    }

    public void frissitTablakat() {
        tagokLista.setAll(tagDAO.findAll());
        presbiterekLista.setAll(presbiterDAO.findAll());
        updateTagRowCounter();
        updatePresRowCounter();
    }

    private void setupTableEvents() {
        // Tagok dupla kattintás -> Módosítás
        tagokTablazat.setRowFactory(tv -> {
            TableRow<Tag> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    openTagModositas(row.getItem());
                }
            });
            return row;
        });

        // Presbiterek dupla kattintás -> Módosítás
        presbiterTablazat.setRowFactory(tv -> {
            TableRow<Presbiter> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    openPresbiterModositas(row.getItem());
                }
            });
            return row;
        });
    }

    // --- Új és Módosítás Dialog-ok ---

    @FXML private void handleUjTagDialog() { openTagModositas(null); }

    private void openTagModositas(Tag tag) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/com/nrgy/uj_tag_dialog.fxml"));
            Parent root = loader.load();
            UjTagController controller = loader.getController();
            controller.setOnSaveCallback(this::frissitTablakat);
            if (tag != null) controller.setTagAdatok(tag);

            Stage stage = new Stage();
            stage.setTitle(tag == null ? "Új tag" : "Tag módosítása");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleUjPresbiterDialog() { openPresbiterModositas(null); }

    private void openPresbiterModositas(Presbiter p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/com/nrgy/uj_presbiter_dialog.fxml"));
            Parent root = loader.load();
            UjPresbiterController controller = loader.getController();
            controller.setOnSaveCallback(this::frissitTablakat);
            if (p != null) controller.setPresbiterAdatok(p);

            Stage stage = new Stage();
            stage.setTitle(p == null ? "Új presbiter" : "Presbiter módosítása");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- Export Dialog megnyitása ---

    @FXML
    private void handleExportDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/com/nrgy/export_dialog.fxml"));
            Parent root = loader.load();
            ExportController controller = loader.getController();

            // Adatok átadása az exportáláshoz
            controller.setAdatok(tagokLista, presbiterekLista);

            Stage stage = new Stage();
            stage.setTitle("Exportálási beállítások");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- Törlés Funkció ---

    @FXML
    private void handleTagTorles() {
        Tag kijelolt = tagokTablazat.getSelectionModel().getSelectedItem();
        if (kijelolt == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Biztosan törli: " + kijelolt.getNev() + "?", ButtonType.YES, ButtonType.NO);
        if (alert.showAndWait().get() == ButtonType.YES) {
            tagDAO.delete(kijelolt.getId());
            frissitTablakat();
        }
    }

    // --- Keresés (FilteredList) ---

    private void setupKereso() {
        FilteredList<Tag> filteredData = new FilteredList<>(tagokLista, p -> true);
        tagKeresoField.textProperty().addListener((obs, oldV, newV) -> {
            filteredData.setPredicate(tag -> {
                if (newV == null || newV.isEmpty()) return true;
                String lower = newV.toLowerCase();
                String oszlop = tagKeresoOszlopCombo.getValue();

                return switch (oszlop) {
                    case "Név" -> tag.getNev().toLowerCase().contains(lower);
                    case "Nem" -> tag.getNem().toLowerCase().startsWith(lower);
                    case "Kor" -> String.valueOf(tag.getEletkor()).contains(lower);
                    case "Szül. Idő" -> tag.getSzul_ido().contains(lower);
                    case "Szül. Hely" -> tag.getSzul_hely().toLowerCase().contains(lower);
                    case "Utca" -> tag.getUtcaNeve().toLowerCase().contains(lower);
                    case "Hsz" -> tag.getHazszam().toLowerCase().contains(lower);
                    case "Telefon" -> tag.getTelefonszam().contains(lower);
                    case "EFJ" -> (tag.isEfj_befizetes() ? "igen" : "nem").contains(lower);
                    case "Presbitere" -> tag.getPresbiterNeve().toLowerCase().contains(lower);
                    case "Megjegyzés" -> tag.getMegjegyzes().toLowerCase().contains(lower);
                    default -> false;
                };
            });
            updateTagRowCounter();
        });

        SortedList<Tag> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tagokTablazat.comparatorProperty());
        tagokTablazat.setItems(sortedData);
        updateTagRowCounter();
    }

    private void setupPresbiterKereso() {
        FilteredList<Presbiter> filteredData = new FilteredList<>(presbiterekLista, p -> true);
        presKeresoField.textProperty().addListener((obs, oldV, newV) -> {
            filteredData.setPredicate(p -> {
                if (newV == null || newV.isEmpty()) return true;
                String lower = newV.toLowerCase();
                String oszlop = presKeresoOszlopCombo.getValue();

                return switch (oszlop) {
                    case "Név" -> p.getNev().toLowerCase().contains(lower);
                    case "Nem" -> p.getNem().toLowerCase().startsWith(lower);
                    case "Kor" -> String.valueOf(p.getEletkor()).contains(lower);
                    case "Szül. Idő" -> p.getSzul_ido().contains(lower);
                    case "Szül. Hely" -> p.getSzul_hely().toLowerCase().contains(lower);
                    case "Utca" -> p.getUtcaNeve().toLowerCase().contains(lower);
                    case "Hsz" -> p.getHazszam().toLowerCase().contains(lower);
                    case "Telefon" -> p.getTelefonszam().contains(lower);
                    case "EFJ" -> (p.isEfj_befizetes() ? "igen" : "nem").contains(lower);
                    case "Beiktatva" -> String.valueOf(p.getBeiktatas_eve()).contains(lower);
                    case "Megjegyzés" -> p.getMegjegyzes().toLowerCase().contains(lower);
                    default -> false;
                };
            });
            updatePresRowCounter();
        });

        SortedList<Presbiter> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(presbiterTablazat.comparatorProperty());
        presbiterTablazat.setItems(sortedData);
        updatePresRowCounter();
    }

    private void updateTagRowCounter() {
        // Ha több fülünk van, érdemes azt a táblázatot számolni, ami épp látszik
        // Vagy egyszerűen a tagok/presbiterek aktuális (szűrt) listáját
        int count = tagokTablazat.getItems().size();
        tagSorSzamlalo.setText(String.valueOf(count));
    }

    private void updatePresRowCounter() {
        // Ha több fülünk van, érdemes azt a táblázatot számolni, ami épp látszik
        // Vagy egyszerűen a tagok/presbiterek aktuális (szűrt) listáját
        int count = presbiterTablazat.getItems().size();
        presSorSzamlalo.setText(String.valueOf(count));
    }




    // --- Stílus és Kilépés ---
    @FXML private void changeToLight() { Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet()); }
    @FXML private void changeToDark() { Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet()); }
    @FXML private void handleExit() { System.exit(0); }
}