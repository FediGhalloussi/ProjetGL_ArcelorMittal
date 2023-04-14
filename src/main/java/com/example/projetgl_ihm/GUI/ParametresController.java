package com.example.projetgl_ihm.GUI;

import java.io.IOException;
import java.util.List;

import com.example.projetgl_ihm.GUI.DashboardOuvrierController;
import com.example.projetgl_ihm.Models.Employee;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;

import static com.example.projetgl_ihm.Database.LoginUtil.*;

public class ParametresController {


    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private ListView<String> userList;

    @FXML
    private TextField userField;

    @FXML
    private ListView<String> userRightsList;

    @FXML
    private Button standButton;
    @FXML
    private Button addButton;

    @FXML
    private TextField inputRangeField;

    @FXML
    void handleAddUser() {
        // Code to add user

    }

    @FXML
    void handleSearchUser() {
        // Code to search for user
    }

    @FXML
    void handleAddRight() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(addButton.scaleXProperty(), 1.0), new KeyValue(addButton.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.millis(100), new KeyValue(addButton.scaleXProperty(), 1.2), new KeyValue(addButton.scaleYProperty(), 1.2)),
                new KeyFrame(Duration.millis(200), new KeyValue(addButton.scaleXProperty(), 0.8), new KeyValue(addButton.scaleYProperty(), 0.8)),
                new KeyFrame(Duration.millis(300), new KeyValue(addButton.scaleXProperty(), 1.1), new KeyValue(addButton.scaleYProperty(), 1.1)),
                new KeyFrame(Duration.millis(400), new KeyValue(addButton.scaleXProperty(), 0.9), new KeyValue(addButton.scaleYProperty(), 0.9)),
                new KeyFrame(Duration.millis(500), new KeyValue(addButton.scaleXProperty(), 1.0), new KeyValue(addButton.scaleYProperty(), 1.0))
        );
        timeline.play();
        // Code to add user right
        String username = usernameField.getText();
        String password = passwordField.getText();

        registerUser(username, password,"worker");
    }

    @FXML
    void handleRemoveRight() {
        // Code to remove user right
    }

    @FXML
    void handleToggleStand() {
        // Code to enable/disable stand
    }

    @FXML
    void handleSaveInputRange() {
        // Code to save level 2 input range
    }


    @FXML
    private Button retourButton;

    @FXML
    public void initialize() {
        retourButton.setOnAction(this::retourAction);
        // Create the ListView and the ObservableList to hold the items
        ObservableList<String> userList = FXCollections.observableArrayList();

        // Retrieve all the employees from the database
        List<Employee> employees = getAllUsers();

        // Add the usernames of the employees to the userList
        for (Employee employee : employees) {
            userList.add(employee.getUsername() + " : " + employee.getGrade());
        }

        // Set the items of the ListView to the userList
        userRightsList.setItems(userList);
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