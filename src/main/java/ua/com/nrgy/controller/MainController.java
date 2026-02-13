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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ua.com.nrgy.model.*;

import java.io.IOException;
import java.util.List;

public class MainController {

    // Táblázatok
    @FXML private TableView<Tag> tagokTablazat;
    @FXML private TableView<Presbiter> presbiterTablazat;

    // Oszlopok (Tagok)
    @FXML private TableColumn<Tag, String> tagNevOszlop;
    @FXML private TableColumn<Tag, String> tagCimOszlop;
    @FXML private TableColumn<Tag, String> tagSzulIdoOszlop;
    @FXML private TableColumn<Tag, String> tagSzulHelyOszlop;
    @FXML private TableColumn<Tag, String> tagPresbitereOszlop;
    @FXML private TableColumn<Tag, String> tagTelOszlop;
    @FXML private TableColumn<Tag, Boolean> tagEfjOszlop;

    // Oszlopok (Presbiterek)
    @FXML private TableColumn<Presbiter, String> presNevOszlop;
    @FXML private TableColumn<Presbiter, String> presSzulIdoOszlop;
    @FXML private TableColumn<Presbiter, String> presSzulHelyOszlop;
    @FXML private TableColumn<Presbiter, String> presCimOszlop;
    @FXML private TableColumn<Presbiter, String> presTelOszlop;
    @FXML private TableColumn<Presbiter, Boolean> presEfjOszlop;
    @FXML private TableColumn<Presbiter, Integer> presBeiktatasOszlop;
    @FXML private TableColumn<Presbiter, String> presUtcaiOszlop;

    @FXML private ComboBox<String> presKeresoOszlopCombo;
    @FXML private TextField presKeresoField;

    // Keresés és szűrés elemei
    @FXML private ComboBox<String> tagKeresoOszlopCombo;
    @FXML private TextField tagKeresoField; // Ez hiányzott a kódodból!

    private final TagDAO tagDAO = new TagDAO();
    private final ObservableList<Tag> tagokLista = FXCollections.observableArrayList();
    private final PresbiterDAO presbiterDAO = new PresbiterDAO();
    private final ObservableList<Presbiter> presbiterekLista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();

        // Táblázat beállítások
        tagokTablazat.setFixedCellSize(38);
        presbiterTablazat.setFixedCellSize(38);
        presbiterTablazat.setItems(presbiterekLista);
        tagKeresoOszlopCombo.getItems().clear(); // Biztonság kedvéért ürítjük
        tagKeresoOszlopCombo.getItems().addAll(
                "Név",
                "Szül. Idő",
                "Szül. Hely",
                "Lakcím",
                "Telefon",
                "Presbitere"
        );
        tagKeresoOszlopCombo.getSelectionModel().selectFirst();

// Presbiter kereső Combo feltöltése az összes oszloppal
        presKeresoOszlopCombo.getItems().clear();
        presKeresoOszlopCombo.getItems().addAll(
                "Név",
                "Szül. Idő",
                "Szül. Hely",
                "Lakcím",
                "Telefon",
                "Beiktatva",
                "Körzet/Utcai"
        );
        presKeresoOszlopCombo.getSelectionModel().selectFirst();

        setupKereso();
        setupPresbiterKereso();

        // Kereső logika beállítása (FONTOS: az Items beállítása előtt!)
        setupKereso();

        frissitTablakat();
    }

    private void setupTableColumns() {
        // Tagok oszlopai összekötése
        tagNevOszlop.setCellValueFactory(c -> c.getValue().nevProperty());
        tagSzulIdoOszlop.setCellValueFactory(c -> c.getValue().szul_idoProperty());
        tagSzulHelyOszlop.setCellValueFactory(c -> c.getValue().szul_helyProperty());
        tagCimOszlop.setCellValueFactory(c -> c.getValue().lakcimProperty());
        tagTelOszlop.setCellValueFactory(c -> c.getValue().telefonszamProperty());
        tagEfjOszlop.setCellValueFactory(c -> c.getValue().efj_befizetesProperty().asObject());
        tagPresbitereOszlop.setCellValueFactory(c -> c.getValue().presbiterNeveProperty());

        // Presbiterek oszlopai összekötése
        presNevOszlop.setCellValueFactory(c -> c.getValue().nevProperty());
        presSzulIdoOszlop.setCellValueFactory(c -> c.getValue().szul_idoProperty());
        presSzulHelyOszlop.setCellValueFactory(c -> c.getValue().szul_helyProperty());
        presCimOszlop.setCellValueFactory(c -> c.getValue().lakcimProperty());
        presTelOszlop.setCellValueFactory(c -> c.getValue().telefonszamProperty());
        presEfjOszlop.setCellValueFactory(c -> c.getValue().efj_befizetesProperty().asObject());
        presBeiktatasOszlop.setCellValueFactory(c -> c.getValue().beiktatas_eveProperty().asObject());
        presUtcaiOszlop.setCellValueFactory(c -> c.getValue().utcaiProperty());
    }

    public void frissitTablakat() {
        tagokLista.setAll(tagDAO.findAll());
        presbiterekLista.setAll(presbiterDAO.findAll());
        System.out.println("Adatok betöltve: " + tagokLista.size() + " tag.");
    }

    private void setupKereso() {
        // FilteredList létrehozása az eredeti listából
        FilteredList<Tag> filteredData = new FilteredList<>(tagokLista, p -> true);

        // Szövegmező változás figyelése
        tagKeresoField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(tag -> {
                if (newVal == null || newVal.isEmpty()) return true;

                String lowerCaseFilter = newVal.toLowerCase();
                String oszlop = tagKeresoOszlopCombo.getValue();

                return switch (oszlop) {
                    case "Név" -> tag.getNev().toLowerCase().contains(lowerCaseFilter);
                    case "Lakcím" -> tag.getLakcim().toLowerCase().contains(lowerCaseFilter);
                    case "Telefon" -> tag.getTelefonszam().toLowerCase().contains(lowerCaseFilter);
                    case "Szül. Hely" -> tag.getSzul_hely().toLowerCase().contains(lowerCaseFilter);
                    case "Szül. Idő" -> tag.getSzul_ido().toLowerCase().contains(lowerCaseFilter);
                    case "Presbitere" -> tag.getPresbiterNeve().toLowerCase().contains(lowerCaseFilter);
                    default -> false;
                };
            });
        });

        // Combo váltáskor frissítés
        tagKeresoOszlopCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            String filter = tagKeresoField.getText();
            tagKeresoField.setText("");
            tagKeresoField.setText(filter);
        });

        // SortedList a rendezhetőségért
        SortedList<Tag> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tagokTablazat.comparatorProperty());
        tagokTablazat.setItems(sortedData);
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

    private void setupPresbiterKereso() {
        FilteredList<Presbiter> filteredData = new FilteredList<>(presbiterekLista, p -> true);

        presKeresoField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(presbiter -> {
                if (newVal == null || newVal.isEmpty()) return true;

                String lowerCaseFilter = newVal.toLowerCase();
                String oszlop = presKeresoOszlopCombo.getValue();

                return switch (oszlop) {
                    case "Név" -> presbiter.getNev().toLowerCase().contains(lowerCaseFilter);
                    case "Szül. Idő" -> presbiter.getSzul_ido().toLowerCase().contains(lowerCaseFilter);
                    case "Szül. Hely" -> presbiter.getSzul_hely().toLowerCase().contains(lowerCaseFilter);
                    case "Lakcím" -> presbiter.getLakcim().toLowerCase().contains(lowerCaseFilter);
                    case "Telefon" -> presbiter.getTelefonszam().toLowerCase().contains(lowerCaseFilter);
                    case "Beiktatva" -> String.valueOf(presbiter.getBeiktatas_eve()).contains(lowerCaseFilter);
                    case "Körzet/Utcai" -> presbiter.getUtcai().toLowerCase().contains(lowerCaseFilter);
                    default -> false;
                };
            });
        });

        // Combo váltáskor a keresés újraindítása
        presKeresoOszlopCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            String filter = presKeresoField.getText();
            presKeresoField.setText("");
            presKeresoField.setText(filter);
        });

        SortedList<Presbiter> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(presbiterTablazat.comparatorProperty());
        presbiterTablazat.setItems(sortedData);
    }

    @FXML
    private void handleUjPresbiterDialog() {
        System.out.println("Új presbiter hozzáadása ablak megnyitása...");
    }

    @FXML
    private void changeToLight() {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
    }

    @FXML
    private void changeToDark() {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}