package com.example.projetgl_ihm.GUI;
import com.example.projetgl_ihm.Models.Engineer;
import com.example.projetgl_ihm.Models.Worker;
import com.example.projetgl_ihm.amine.base_de_donnée.H2DatabaseConnection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;

import static java.rmi.Naming.lookup;

/**
 * Cette classe représente le controlleur de la page qui s'affichent après le login
 */
public class DashboardOuvrierController {
    private H2DatabaseConnection db = new H2DatabaseConnection();
    @FXML
    private Button ParametresButton;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private ChoiceBox numAtelierChoiceBox;
    @FXML
    private ChoiceBox numEquipementChoiceBox;
    @FXML
    private CheckBox sigmaCheckBox;
    @FXML
    private CheckBox speedCheckBox;
    @FXML
    private CheckBox frictionCheckBox;

    private Timer timer;
    // Ajouter une variable pour indiquer si la thread a déjà été lancée
    private boolean threadLancee = false;



    @FXML
    private Text nomLabel ;
    @FXML
    private ChoiceBox ComputeTimeChoiceBox;

    String grade;

    @FXML
    private Button logoutButton;


    @FXML
    public void initialize() {
        ParametresButton.setOnAction(this::ouvrirParametres);
        logoutButton.setOnAction(this::handleLogoutButton);

    }

    public void setUsername(String username) {
        nomLabel.setText(username);
    }

    /**
     * Méthode pour ce déconnecter
     */
    @FXML
    private void handleLogoutButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);

            // Récupérer le stage actuel à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setWidth(1120);
            stage.setHeight(630);
            stage.setResizable(false);// Remplacer la scène actuelle par la scène de connexion
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Ourvrir l'onglet paramètre en fonction de l'autorisation
     */
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
    /**
     * Afficher les graphiques, en lancaant une thread qui s'excutent toutes les 1 secondes
     *
     * Les valeurs pour le stand F3 ne sont correct qu'a partir de la 8 secondes
     *
     * Pour changer d'équipement en cours il faut relancer l'applicaiton, nous n'avons pas eu le temps de fixer ce bug
     */
    public void afficherGraphiques(ActionEvent actionEvent) throws SQLException, InterruptedException {
        db.connection();
        String stand_id;
        int num_id;
        stand_id = (String) numAtelierChoiceBox.getValue();
        num_id = Integer.parseInt((String) numEquipementChoiceBox.getValue());


        // Effacer les données précédentes du graphique
        lineChart.getData().clear();

        // Créer les axes du graphique
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();

            // Configurer le graphique
            lineChart.setTitle("Courbes");
            lineChart.setCreateSymbols(false);
            lineChart.setLegendVisible(false);

            // Créer une liste de séries de données
            List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();

            // Initialiser la variable de temps courant
            int currentTime = 0;

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    double time = 0;
                    while (true) {
                        db.connection();
                        db.Average(stand_id,num_id,time);
                        time += 1;
                        // Vérifier si la tâche a été annulée
                        if (isCancelled()) {
                            break;
                        }

                        // Récupérer les données de la base de données
                        db.connection();
                        db.getGraphiques(stand_id, num_id);
                        // Créer une Map pour stocker les séries de données
                        Map<String, XYChart.Series<Number, Number>> seriesMap = new HashMap<>();

                        // Ajouter des données au graphique en fonction des cases à cocher sélectionnées
                        if (sigmaCheckBox.isSelected()) {
                            String seriesName = "Sigma";
                            XYChart.Series<Number, Number> series = seriesMap.get(seriesName);
                            if (series == null) {
                                series = new XYChart.Series<>();
                                series.setName(seriesName);
                                seriesMap.put(seriesName, series);
                            }
                            for (int i = 0; i < db.getTimeMean().size(); i++) {
                                series.getData().add(new XYChart.Data<>(db.getTimeMean().get(i), db.getSigmaMean().get(i)));
                            }
                        }

                        if (frictionCheckBox.isSelected()) {
                            String seriesName = "Friction";
                            XYChart.Series<Number, Number> series = seriesMap.get(seriesName);
                            if (series == null) {
                                series = new XYChart.Series<>();
                                series.setName(seriesName);
                                seriesMap.put(seriesName, series);
                            }
                            for (int i = 0; i < db.getTimeMean().size(); i++) {
                                series.getData().add(new XYChart.Data<>(db.getTimeMean().get(i), db.getFrictionMean().get(i)));
                            }
                        }

                        if (speedCheckBox.isSelected()) {
                            String seriesName = "Speed";
                            XYChart.Series<Number, Number> series = seriesMap.get(seriesName);
                            if (series == null) {
                                series = new XYChart.Series<>();
                                series.setName(seriesName);
                                seriesMap.put(seriesName, series);
                            }
                            for (int i = 0; i < db.getTimeMean().size(); i++) {
                                series.getData().add(new XYChart.Data<>(db.getTimeMean().get(i), db.getSpeedMean().get(i)));
                            }
                        }

// Mettre à jour le graphique sur le thread de l'interface utilisateur
                        Platform.runLater(() -> {
                            lineChart.getData().clear();
                            for (XYChart.Series<Number, Number> series : seriesMap.values()) {
                                lineChart.getData().add(series);
                            }
                        });
                    Thread.sleep(1000);
                    }
                    return null;
                }
            };
        if (!threadLancee) {
            Thread taskThread = new Thread(task);
            taskThread.start();
            threadLancee = true;
        } else {

        }

    }
    public void setGrade(String grade) {
        this.grade = grade;
        if (grade.equals("worker")) {
            ParametresButton.setVisible(false);
        }
    }
}