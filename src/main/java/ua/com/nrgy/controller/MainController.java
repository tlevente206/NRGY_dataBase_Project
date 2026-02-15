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
import java.util.List;

public class MainController {

    // --- Táblázatok ---
    @FXML private TableView<Tag> tagokTablazat;
    @FXML private TableView<Presbiter> presbiterTablazat;

    // --- Oszlopok (Tagok) ---
    @FXML private TableColumn<Tag, String> tagNevOszlop;
    @FXML private TableColumn<Tag, String> tagNemOszlop; // ÚJ
    @FXML private TableColumn<Tag, String> tagSzulIdoOszlop;
    @FXML private TableColumn<Tag, String> tagSzulHelyOszlop;
    @FXML private TableColumn<Tag, String> tagUtcaOszlop; // MÓDOSÍTOTT (utcaNeveProperty)
    @FXML private TableColumn<Tag, String> tagHazszamOszlop; // ÚJ
    @FXML private TableColumn<Tag, String> tagTelOszlop;
    @FXML private TableColumn<Tag, Boolean> tagEfjOszlop;
    @FXML private TableColumn<Tag, String> tagPresbitereOszlop;
    @FXML private TableColumn<Tag, String> tagMegjegyzesOszlop; // ÚJ

    // --- Oszlopok (Presbiterek) ---
    @FXML private TableColumn<Presbiter, String> presNevOszlop;
    @FXML private TableColumn<Presbiter, String> presNemOszlop; // ÚJ
    @FXML private TableColumn<Presbiter, String> presSzulIdoOszlop;
    @FXML private TableColumn<Presbiter, String> presSzulHelyOszlop;
    @FXML private TableColumn<Presbiter, String> presUtcaOszlop; // MÓDOSÍTOTT
    @FXML private TableColumn<Presbiter, String> presHazszamOszlop; // ÚJ
    @FXML private TableColumn<Presbiter, String> presTelOszlop;
    @FXML private TableColumn<Presbiter, Boolean> presEfjOszlop;
    @FXML private TableColumn<Presbiter, Integer> presBeiktatasOszlop;
    @FXML private TableColumn<Presbiter, String> presMegjegyzesOszlop; // ÚJ

    // --- Keresés elemei ---
    @FXML private ComboBox<String> tagKeresoOszlopCombo;
    @FXML private TextField tagKeresoField;
    @FXML private ComboBox<String> presKeresoOszlopCombo;
    @FXML private TextField presKeresoField;

    @FXML private TableColumn<Tag, Integer> tagEletkorOszlop;
    @FXML private TableColumn<Presbiter, Integer> presEletkorOszlop;

    private final TagDAO tagDAO = new TagDAO();
    private final ObservableList<Tag> tagokLista = FXCollections.observableArrayList();
    private final PresbiterDAO presbiterDAO = new PresbiterDAO();
    private final ObservableList<Presbiter> presbiterekLista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();

        tagokTablazat.setFixedCellSize(38);
        presbiterTablazat.setFixedCellSize(38);

        // Bővített lista a "Szül. Idő" opcióval
        tagKeresoOszlopCombo.getItems().setAll("Név", "Nem", "Szül. Idő", "Szül. Hely", "Utca", "Telefon", "Presbitere", "Megjegyzés");
        tagKeresoOszlopCombo.getSelectionModel().selectFirst();

        // Presbitereknél is érdemes lehet
        presKeresoOszlopCombo.getItems().setAll("Név", "Nem", "Szül. Idő", "Szül. Hely", "Utca", "Beiktatva", "Megjegyzés");
        presKeresoOszlopCombo.getSelectionModel().selectFirst();

        setupKereso();
        setupPresbiterKereso();

        setupTableEvents();
        frissitTablakat();
    }

    private void setupTableColumns() {
        // Tagok oszlop bekötés
        tagNevOszlop.setCellValueFactory(c -> c.getValue().nevProperty());
        tagNemOszlop.setCellValueFactory(c -> c.getValue().nemProperty());
        tagSzulIdoOszlop.setCellValueFactory(c -> c.getValue().szul_idoProperty());
        tagEletkorOszlop.setCellValueFactory(c -> c.getValue().eletkorProperty().asObject());
        tagSzulHelyOszlop.setCellValueFactory(c -> c.getValue().szul_helyProperty());
        tagUtcaOszlop.setCellValueFactory(c -> c.getValue().utcaNeveProperty());
        tagHazszamOszlop.setCellValueFactory(c -> c.getValue().hazszamProperty());
        tagTelOszlop.setCellValueFactory(c -> c.getValue().telefonszamProperty());
        tagEfjOszlop.setCellValueFactory(c -> c.getValue().efj_befizetesProperty().asObject());
        tagPresbitereOszlop.setCellValueFactory(c -> c.getValue().presbiterNeveProperty());
        tagMegjegyzesOszlop.setCellValueFactory(c -> c.getValue().megjegyzesProperty());

        // Presbiterek oszlop bekötés
        presNevOszlop.setCellValueFactory(c -> c.getValue().nevProperty());
        presNemOszlop.setCellValueFactory(c -> c.getValue().nemProperty());
        presSzulIdoOszlop.setCellValueFactory(c -> c.getValue().szul_idoProperty());
        presEletkorOszlop.setCellValueFactory(c -> c.getValue().eletkorProperty().asObject());
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
        System.out.println("Adatok frissítve.");
    }

    private void setupKereso() {
        FilteredList<Tag> filteredData = new FilteredList<>(tagokLista, p -> true);
        tagKeresoField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(tag -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                String oszlop = tagKeresoOszlopCombo.getValue();

                return switch (oszlop) {
                    case "Név" -> tag.getNev().toLowerCase().contains(lower);
                    case "Nem" -> tag.getNem().toLowerCase().startsWith(lower);
                    case "Szül. Idő" -> tag.getSzul_ido().toLowerCase().contains(lower); // Dátum keresés
                    case "Szül. Hely" -> tag.getSzul_hely().toLowerCase().contains(lower);
                    case "Utca" -> tag.getUtcaNeve().toLowerCase().contains(lower);
                    case "Telefon" -> tag.getTelefonszam().contains(lower);
                    case "Presbitere" -> tag.getPresbiterNeve().toLowerCase().contains(lower);
                    case "Megjegyzés" -> tag.getMegjegyzes().toLowerCase().contains(lower);
                    default -> false;
                };
            });
        });

        SortedList<Tag> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tagokTablazat.comparatorProperty());
        tagokTablazat.setItems(sortedData);
    }

    private void setupPresbiterKereso() {
        FilteredList<Presbiter> filteredData = new FilteredList<>(presbiterekLista, p -> true);
        presKeresoField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(p -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                String oszlop = presKeresoOszlopCombo.getValue();

                return switch (oszlop) {
                    case "Név" -> p.getNev().toLowerCase().contains(lower);
                    case "Nem" -> p.getNem().toLowerCase().startsWith(lower);
                    case "Szül. Idő" -> p.getSzul_ido().toLowerCase().contains(lower); // Dátum keresés itt is
                    case "Szül. Hely" -> p.getSzul_hely().toLowerCase().contains(lower);
                    case "Utca" -> p.getUtcaNeve().toLowerCase().contains(lower);
                    case "Beiktatva" -> String.valueOf(p.getBeiktatas_eve()).contains(lower);
                    case "Megjegyzés" -> p.getMegjegyzes().toLowerCase().contains(lower);
                    default -> false;
                };
            });
        });

        SortedList<Presbiter> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(presbiterTablazat.comparatorProperty());
        presbiterTablazat.setItems(sortedData);
    }

    @FXML
    private void handleUjTagDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/com/nrgy/uj_tag_dialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Új tag felvétele");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            UjTagController controller = loader.getController();
            controller.setOnSaveCallback(this::frissitTablakat);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void changeToLight() { Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet()); }
    @FXML private void changeToDark() { Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet()); }
    @FXML private void handleExit() { System.exit(0); }

    @FXML
    private void handleTagTorles() {
        // 1. Megnézzük, van-e kijelölt sor
        Tag kijeloltTag = tagokTablazat.getSelectionModel().getSelectedItem();

        if (kijeloltTag == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nincs kijelölés");
            alert.setHeaderText(null);
            alert.setContentText("Kérjük, válasszon ki egy tagot a táblázatból a törléshez!");
            alert.showAndWait();
            return;
        }

        // 2. Megerősítést kérünk
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Törlés megerősítése");
        confirm.setHeaderText("Biztosan törölni szeretné?");
        confirm.setContentText("A következő tag adatai véglegesen törlődnek: " + kijeloltTag.getNev());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            // 3. Törlés az adatbázisból
            tagDAO.delete(kijeloltTag.getId());

            // 4. Táblázat frissítése
            frissitTablakat();
        }
    }


    private void openModositasDialog(Tag tag) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ua/com/nrgy/uj_tag_dialog.fxml"));
            Parent root = loader.load();

            UjTagController controller = loader.getController();
            controller.setTagAdatok(tag); // Adatok átadása!
            controller.setOnSaveCallback(this::frissitTablakat);

            Stage stage = new Stage();
            stage.setTitle("Tag adatainak módosítása");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleUjPresbiterDialog() {
        openPresbiterDialog(null);
    }

    private void openPresbiterDialog(Presbiter p) {
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

    // A setupTableEvents() metódust bővítsd ki:
    private void setupTableEvents() {
        // Tagok dupla kattintás (marad a régi)
        tagokTablazat.setRowFactory(tv -> {
            TableRow<Tag> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    openModositasDialog(row.getItem());
                }
            });
            return row;
        });

        // Presbiterek dupla kattintás (ÚJ!)
        presbiterTablazat.setRowFactory(tv -> {
            TableRow<Presbiter> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    openPresbiterDialog(row.getItem());
                }
            });
            return row;
        });
    }
}