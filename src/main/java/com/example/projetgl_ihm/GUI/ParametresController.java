package com.example.projetgl_ihm.GUI;

import java.io.IOException;

import com.example.projetgl_ihm.GUI.DashboardOuvrierController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;

public class ParametresController {
    @FXML
    private Button retourButton;

    @FXML
    public void initialize() {
        retourButton.setOnAction(this::retourAction);
    }


    @FXML
    private void retourAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DashboardOuvrier.fxml"));
            Parent dashboardRoot = loader.load();
            Scene dashboardScene = new Scene(dashboardRoot);

            // Récupérer le stage actuel à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer la scène actuelle par la scène du tableau de bord
            stage.setScene(dashboardScene);

            // Forcer la réinitialisation du contrôleur de la scène du tableau de bord
            DashboardOuvrierController dashboardController = loader.getController();
            dashboardController.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}