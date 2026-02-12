package ua.com.nrgy.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainController {

    @FXML
    private TableView<?> tagokTablazat; // Egyelőre üres, amíg nincs Model

    @FXML
    private void handleUjTag() {
        System.out.println("Gomb megnyomva! Itt nyílna meg majd az ablak.");
    }
}