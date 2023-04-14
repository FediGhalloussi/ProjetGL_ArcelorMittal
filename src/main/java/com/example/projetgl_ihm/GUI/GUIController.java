package com.example.projetgl_ihm.GUI;

import com.example.projetgl_ihm.Database.LoginUtil;
import com.example.projetgl_ihm.Models.Employee;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class GUIController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;
    @FXML
    private Button retryButton;

    @FXML
    private Pane loadingPane;
    @FXML
    private Pane loginPane;

    @FXML
    private Pane errorPane;

    @FXML
    private Text errorLabel;

    public void loginAction() {
        // Disable the login button and show the loading pane
        loginButton.setDisable(true);
        loadingPane.setVisible(true);

        // Simulate a login process
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.equals("user") && password.equals("password")) {
                // Login successful
                System.out.println("Login successful");
            } else {
                // Login failed
                errorLabel.setText("Invalid username or password");
                errorPane.setVisible(true);
                errorLabel.setVisible(true);
            }

            // Hide the loading pane and enable the login button
            loadingPane.setVisible(false);
            loginButton.setDisable(false);
        });

        pause.play();
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        //Employee employee = LoginUtil.authenticateUser(username, password);
        if (/*employee != null*/ username.equals("admin")) {
            loadingPane.setVisible(true);
            // Affiche le loadingPane pendant que le traitement de la connexion est en cours
            // Animation de zoom-out sur le bouton de réessai
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(loginButton.scaleXProperty(), 1.0), new KeyValue(loginButton.scaleYProperty(), 1.0)),
                    new KeyFrame(Duration.millis(100), new KeyValue(loginButton.scaleXProperty(), 1.2), new KeyValue(loginButton.scaleYProperty(), 1.2)),
                    new KeyFrame(Duration.millis(200), new KeyValue(loginButton.scaleXProperty(), 0.8), new KeyValue(loginButton.scaleYProperty(), 0.8)),
                    new KeyFrame(Duration.millis(300), new KeyValue(loginButton.scaleXProperty(), 1.1), new KeyValue(loginButton.scaleYProperty(), 1.1)),
                    new KeyFrame(Duration.millis(400), new KeyValue(loginButton.scaleXProperty(), 0.9), new KeyValue(loginButton.scaleYProperty(), 0.9)),
                    new KeyFrame(Duration.millis(500), new KeyValue(loginButton.scaleXProperty(), 1.0), new KeyValue(loginButton.scaleYProperty(), 1.0))
            );
            timeline.play();
            loadingPane.setVisible(true);
            System.out.println("test");
            // Simule un délai de traitement
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                // Masque le loadingPane une fois que la connexion est réussie
                loadingPane.setVisible(false);
                // Affiche un message de bienvenue
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Connexion réussie");
//                alert.setHeaderText("Bienvenue, " + username + " !");
//                alert.showAndWait();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("DashboardOuvrier.fxml"));
                Parent ParametresRoot = null;
                try {
                    ParametresRoot = loader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                DashboardOuvrierController controller = loader.getController();
                controller.setUsername(username);

                Scene ParametresScene = new Scene(ParametresRoot);

                // Récupérer le stage actuel à partir de l'événement
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setScene(ParametresScene); // Remplacer la scène actuelle par la scène des paramètres


            });
            delay.play();
        } else {
            // Affiche le errorPane si les informations de connexion sont incorrectes
            errorPane.setVisible(true);
            errorPane.setMouseTransparent(false);

        }

    }

    @FXML
    private void handleRetryButton(ActionEvent event) {
        // Réinitialisation des champs de saisie
        usernameField.setText("");
        passwordField.setText("");

        // Masquage du message d'erreur
        //errorLabel.setVisible(false);

        // Animation de zoom-out sur le bouton de réessai
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(retryButton.scaleXProperty(), 1.0), new KeyValue(retryButton.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.millis(100), new KeyValue(retryButton.scaleXProperty(), 1.2), new KeyValue(retryButton.scaleYProperty(), 1.2)),
                new KeyFrame(Duration.millis(200), new KeyValue(retryButton.scaleXProperty(), 0.8), new KeyValue(retryButton.scaleYProperty(), 0.8)),
                new KeyFrame(Duration.millis(300), new KeyValue(retryButton.scaleXProperty(), 1.1), new KeyValue(retryButton.scaleYProperty(), 1.1)),
                new KeyFrame(Duration.millis(400), new KeyValue(retryButton.scaleXProperty(), 0.9), new KeyValue(retryButton.scaleYProperty(), 0.9)),
                new KeyFrame(Duration.millis(500), new KeyValue(retryButton.scaleXProperty(), 1.0), new KeyValue(retryButton.scaleYProperty(), 1.0))
        );
        timeline.play();
        // Cache le errorPane après 3 secondes
        PauseTransition delay = new PauseTransition(Duration.seconds(.5));
        delay.setOnFinished(e -> {
            errorPane.setVisible(false);
        });
        delay.play();

    }

}

