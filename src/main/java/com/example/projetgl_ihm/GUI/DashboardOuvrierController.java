package com.example.projetgl_ihm.GUI;
import com.example.projetgl_ihm.amine.base_de_donnée.H2DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    @FXML
    private Text nomLabel;

    @FXML
    public void initialize() {
        ParametresButton.setOnAction(this::ouvrirParametres);
    }

    public void setUsername(String username) {
        nomLabel.setText(username);
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

    public void afficherGraphiques(ActionEvent actionEvent) throws SQLException {
        db.connection();
        ArrayList<ArrayList<Double>> resultat = new ArrayList<>();
        String stand_id;
        int num_id;

        stand_id = (String) numAtelierChoiceBox.getValue();
        num_id = Integer.parseInt((String)numEquipementChoiceBox.getValue());
        System.out.println(stand_id + num_id);

        resultat = db.Average(stand_id,num_id,1);

        ArrayList<Double> xtimeListMean = new ArrayList<>();
        ArrayList<Double>sigmaMoyListMean = new ArrayList<>();
        ArrayList<Double> frictionListMean = new ArrayList<>();
        ArrayList<Double> rollingTorqueValue = new ArrayList<>();

        xtimeListMean = resultat.get(0);
        frictionListMean = resultat.get(1);
        sigmaMoyListMean = resultat.get(2);
        rollingTorqueValue = resultat.get(3);

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
// Ajouter des données au graphique en fonction des cases à cocher sélectionnées
        if (sigmaCheckBox.isSelected()) {
            XYChart.Series<Number, Number> sigmaSeries = new XYChart.Series<>();
            sigmaSeries.setName("Sigma");
            for (int i = 0; i < xtimeListMean.size(); i++) {
                sigmaSeries.getData().add(new XYChart.Data<>(xtimeListMean.get(i), sigmaMoyListMean.get(i)));
            }
            seriesList.add(sigmaSeries);
        }

        if (frictionCheckBox.isSelected()) {
            XYChart.Series<Number, Number> gammaSeries = new XYChart.Series<>();
            gammaSeries.setName("Friction");
            for (int i = 0; i < xtimeListMean.size(); i++) {
                gammaSeries.getData().add(new XYChart.Data<>(xtimeListMean.get(i), frictionListMean.get(i)));
            }
            seriesList.add(gammaSeries);
        }

        if (speedCheckBox.isSelected()) {
            XYChart.Series<Number, Number> deltaSeries = new XYChart.Series<>();
            deltaSeries.setName("SPEED");

            for (int i = 0; i < xtimeListMean.size(); i++) {
                deltaSeries.getData().add(new XYChart.Data<>(xtimeListMean.get(i), rollingTorqueValue.get(i)));
            }
            seriesList.add(deltaSeries);
        }

        // Ajouter chaque série de données au graphique
        for (XYChart.Series<Number, Number> series : seriesList) {
            lineChart.getData().add(series);
        }
// Réafficher le graphique
        lineChart.setLegendVisible(true);
        lineChart.setVisible(true);
    }

}