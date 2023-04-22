package com.example.projetgl_ihm.GUI;

import com.example.projetgl_ihm.utils.LoginUtil;
import com.example.projetgl_ihm.models.Employee;
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
import java.sql.SQLException;

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
    /**

     Simulates a login process when the login button is clicked.
     Disables the login button and shows the loading pane.
     After a simulated login process of 2 seconds, retrieves the entered username and password
     and checks if they match the expected values ("user" and "password").
     If the login is successful, prints a message to the console.
     If the login fails, shows an error message on the errorLabel and makes the errorPane visible.
     Hides the loading pane and enables the login button after the login process is finished.£

     AT THE END START TO READ CSV WHICH REPRESENT SENSORS DATA
     */
    public void loginAction() throws SQLException, IOException, InterruptedException {
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
        // LANCER LA COMPUTATION DES CAPTEURS A la base de donnée
        // en commentaire car notre base de donnée est déja chargée
       //H2DatabaseConnection data_base = new H2DatabaseConnection();
        //data_base.ReadCSV_Stand("1939351_F2.txt", "\t", "F2 ");
       //data_base.ComputeOrowan("F2",1939351);
        //data_base.ComputeOrowan("F3",1939351);
       // data_base.closeDatabase();
    }
    /**
     * Handles the login button click event.
     * Authenticates the user with the entered username and password using the LoginUtil class.
     * If the authentication is successful, navigates the user to the DashboardOuvrier.fxml file.
     * Otherwise, displays the errorPane with a message.
     *
     * @param event The event triggered by clicking the login button.
     */
    @FXML
    private void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Employee employee = LoginUtil.authenticateUser(username, password);
        if (employee != null) {
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

            // Simule un délai de traitement
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                // Masque le loadingPane une fois que la connexion est réussie
                loadingPane.setVisible(false);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("DashboardOuvrier.fxml"));
                Parent ParametresRoot = null;
                try {
                    ParametresRoot = loader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                DashboardOuvrierController controller = loader.getController();

                Scene ParametresScene = new Scene(ParametresRoot);
                controller.setGrade(employee.getGrade());
                // Récupérer le stage actuel à partir de l'événement
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setScene(ParametresScene); // Remplacer la scène actuelle par la scène des paramètres
                controller.setUsername(username);
            });
            delay.play();
        } else {
            // Affiche le errorPane si les informations de connexion sont incorrectes
            errorPane.setVisible(true);
            errorPane.setMouseTransparent(false);

        }
    }
    /**

     Cette méthode est appelée lorsque l'utilisateur clique sur le bouton "Réessayer" dans le panneau d'erreur. Elle réinitialise les champs de saisie et anime le bouton de réessai avec une séquence de KeyFrames.
     @param event un événement d'action généré lors de l'appui sur le bouton "Réessayer"
     */
    @FXML
    private void handleRetryButton(ActionEvent event) {
        // Réinitialisation des champs de saisie
        usernameField.setText("");
        passwordField.setText("");


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

