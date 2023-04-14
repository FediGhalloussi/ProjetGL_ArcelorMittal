package com.example.projetgl_ihm.GUI;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;


public class DashboardOuvrierController {

    @FXML
    private Button ParametresButton;

    @FXML
    public void initialize() {
        ParametresButton.setOnAction(this::ouvrirParametres);
    }

    @FXML
    private void ouvrirParametres(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Parametres.fxml"));
            Parent ParametresRoot = loader.load();
            Scene ParametresScene = new Scene(ParametresRoot);





            // Récupérer le stage actuel à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(ParametresScene); // Remplacer la scène actuelle par la scène des paramètres
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void afficherGraphiques(ActionEvent actionEvent) {
    }
}