package com.example.projetgl_ihm.amine.base_de_donnée;

import com.example.projetgl_ihm.amine.orowan.OrowanLauncher;

import java.util.ArrayList;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

public class H2DatabaseConnection {
    private Connection conn = null;
    public H2DatabaseConnection(){}

    public void connection(){
        try {
            // Chargement du pilote JDBC H2
            Class.forName("org.h2.Driver");

            // Connexion à la base de données
            String url = "jdbc:h2:tcp://localhost/~/test";
            String user = "projetGL";
            String password = "afa";
            conn = DriverManager.getConnection(url, user, password);

            System.out.println("Connexion à la base de données H2 réussie.");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données H2.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Erreur de chargement du pilote JDBC H2.");
            e.printStackTrace();
        }

    }

    public void closeDatabase(){
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la fermeture de la connexion à la base de données H2.");
            e.printStackTrace();
        }
    }

    public void ReadCSV_Stand(String path, String tab, String stand ){

        try {
            // Ouverture du fichier CSV
            File csvFile = new File(path);
            BufferedReader csvReader = new BufferedReader(new FileReader(csvFile));
            csvReader.readLine();
            // Création de la requête SQL pour l'insertion des données
            String insertQuery = "INSERT INTO FILE_FORMAT (LP,STAND_ID,MATID,XTIME,XLOC,ENTHICK,EXTHICK,ENTENS,EXTENS,ROLLFORCE,FSLIP,DAIAMETER,ROLLED_LENGTH_FOR_WORK_ROLLS,YOUNGMODULUS,BACKUP_ROLL_DIA,ROLLED_LENGTH_FOR_BACKUP_ROLLS,MU,TORQUE,AVERAGESIGMA,INPUTERROR,LUBWFLUP,LUBWFLLO,LUBOILFLUP,LUBOILFLLO,WORK_ROLL_SPEED) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            // Préparation de la requête SQL avec la connexion à la base de données
            PreparedStatement stmt = conn.prepareStatement(insertQuery);

            // Lecture des données du fichier CSV ligne par ligne
            String row;
            while ((row = csvReader.readLine()) != null) {
                row = row.replaceAll("\\s*" + tab + "\\s*", tab);
                row = row.replaceAll(",", ".");
                String[] data = row.split(tab);
                System.out.println(data[2]);
                // Insertion des données dans la base de données
                stmt.setInt(1, Integer.parseInt(data[0]));
                stmt.setString(2, stand);
                stmt.setInt(3, Integer.parseInt(data[1]));
                stmt.setDouble(4, Double.parseDouble(data[2]));
                stmt.setDouble(5, Double.parseDouble(data[3]));
                stmt.setDouble(6, Double.parseDouble(data[4]));
                stmt.setDouble(7, Double.parseDouble(data[5]));
                stmt.setDouble(8, Double.parseDouble(data[6]));
                stmt.setDouble(9, Double.parseDouble(data[7]));
                stmt.setDouble(10, Double.parseDouble(data[8]));
                stmt.setDouble(11, Double.parseDouble(data[9]));
                stmt.setDouble(12, Double.parseDouble(data[10]));
                stmt.setInt(13, Integer.parseInt(data[11]));
                stmt.setDouble(14, Double.parseDouble(data[12]));
                stmt.setDouble(15, Double.parseDouble(data[13]));
                stmt.setInt(16, Integer.parseInt(data[14]));
                stmt.setDouble(17, Double.parseDouble(data[15]));
                stmt.setDouble(18, Double.parseDouble(data[16]));
                stmt.setDouble(19, Double.parseDouble(data[17]));
                stmt.setDouble(20, Double.parseDouble(data[18]));
                stmt.setDouble(21, Double.parseDouble(data[19]));
                stmt.setDouble(22, Double.parseDouble(data[20]));
                stmt.setDouble(23, Double.parseDouble(data[21]));
                stmt.setDouble(24, Double.parseDouble(data[22]));
                stmt.setDouble(25, Double.parseDouble(data[23]));

                stmt.executeUpdate();
            }
            csvReader.close();
            System.out.println("Données insérées avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier CSV.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion des données dans la base de données.");
            e.printStackTrace();
        }
    }
    public void ComputeOrowan(int computeTime, String stand_id, int mat_id) throws SQLException, IOException, InterruptedException {
        double time = 1;
        ArrayList<Double> times = new ArrayList<Double>();
        try {

            // Établir une connexion à la base de données
            String sqlQuery = String.format("SELECT ENTHICK, EXTHICK, ENTENS, EXTENS, DAIAMETER, YOUNGMODULUS, AVERAGESIGMA, MU, ROLLFORCE, FSLIP, XTIME FROM FILE_FORMAT WHERE STAND_ID = '%s' AND MATID = %d ;", stand_id, mat_id);
            // Préparer la requête SQL
            //String sql = "SELECT ENTHICK, EXTHICK, ENTENS, EXTENS, DAIAMETER, YOUNGMODULUS, AVERAGESIGMA, MU, ROLLFORCE, FSLIP,XTIME FROM FILE_FORMAT WHERE XTIME> 1 LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sqlQuery);

            // Exécuter la requête et récupérer les résultats
            ResultSet rs = stmt.executeQuery();

            // Écrire les résultats dans un fichier CSV
            String filename = "com/example/projetgl_ihm/amine/file/orowan/input.csv";
            FileWriter writer = new FileWriter(filename);
            writer.append("Cas\tHe\tHs\tTe\tTs\tDiam_WR\tWRyoung\toffset ini\tmu_ini\tForce\tG\n");
            // Parcourir les résultats et les afficher
            while (rs.next()) {
                double entHick = rs.getDouble("ENTHICK");
                double extHick = rs.getDouble("EXTHICK");
                double entEns = rs.getDouble("ENTENS");
                double extEns = rs.getDouble("EXTENS");
                double diameter = rs.getDouble("DAIAMETER");
                double youngModulus = rs.getDouble("YOUNGMODULUS");
                double averageSigma = rs.getDouble("AVERAGESIGMA");
                double mu = rs.getDouble("MU");
                double rollForce = rs.getDouble("ROLLFORCE");
                double fSlip = rs.getDouble("FSLIP");
                times.add(rs.getDouble("XTIME"));

                String line = String.format("%d\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\n",1, entHick, extHick,entEns, extEns, diameter, youngModulus, averageSigma, mu, rollForce, fSlip);
                line =line.replaceAll(",",".");
                writer.append(line);
                // Faire quelque chose avec les données récupérées, par exemple les afficher
                System.out.println("entHick: " + entHick+ "ext" +extHick+ ", entEns: " + entEns + ", extEns: " + extEns + ", diameter: " + diameter + ", youngModulus: " + youngModulus + ", averageSigma: " + averageSigma + ", mu: " + mu + ", rollForce: " + rollForce + ", fSlip: " + fSlip + ", xTime: " );
            }
            writer.flush();
            writer.close();
            // Fermer la connexion
            rs.close();
            stmt.close();


        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
        OrowanLauncher a = new OrowanLauncher();
        a.launch("com/example/projetgl_ihm/amine/file/orowan/input.csv","com/example/projetgl_ihm/amine/file/orowan/output.csv");
        ReadCSV_CSV_Output("com/example/projetgl_ihm/amine/file/orowan/output.csv","\t", stand_id, mat_id, times);
    }
    public void ReadCSV_CSV_Output(String path, String tab, String stand_id ,int mat_id, ArrayList<Double> times){

        try {
            // Ouverture du fichier CSV
            File csvFile = new File(path);
            BufferedReader csvReader = new BufferedReader(new FileReader(csvFile));
            csvReader.readLine();
            // Création de la requête SQL pour l'insertion des données
            String insertQuery = "INSERT INTO CSV_OUTPUT_FILE (MATID,STAND_ID, XTIME, INT,Errors,OffsetYield,Friction,Rolling_Torque,Sigma_Moy,Sigma_Ini,Sigma_Out,Sigma_Max,FORCE_ERROR,SLIP_ERROR,HAS_CONVERGED) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            // Préparation de la requête SQL avec la connexion à la base de données
            PreparedStatement stmt = conn.prepareStatement(insertQuery);

            // Lecture des données du fichier CSV ligne par ligne
            String row;
            int i = 0;
            while ((row = csvReader.readLine()) != null) {
                row = row.replaceAll(",", ".");
                String[] data = row.split(tab);
                // Insertion des données dans la base de données
                stmt.setInt(1, mat_id);
                stmt.setString(2, stand_id);
                stmt.setDouble(3,times.get(i));
                stmt.setInt(4, Integer.parseInt(data[0]));
                stmt.setString(5, data[1]);
                stmt.setDouble(6, Double.parseDouble(data[2]));
                stmt.setDouble(7, Double.parseDouble(data[3]));
                stmt.setDouble(8, Double.parseDouble(data[4]));
                stmt.setDouble(9, Double.parseDouble(data[5]));
                stmt.setDouble(10, Double.parseDouble(data[6]));
                stmt.setDouble(11, Double.parseDouble(data[7]));
                stmt.setDouble(12, Double.parseDouble(data[8]));
                stmt.setDouble(13, Double.parseDouble(data[9]));
                stmt.setDouble(14, Double.parseDouble(data[10]));
                stmt.setString(15,data[11]);

                stmt.executeUpdate();
                i++;
            }
            csvReader.close();
            System.out.println("Données insérées avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier CSV.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion des données dans la base de données.");
            e.printStackTrace();
        }

    }

    public ArrayList<ArrayList<Double>> Average(String Stand_ID, int mat_id, int computeTime) throws SQLException {

        ArrayList<ArrayList<Double>> resultat = new ArrayList<>();

        ArrayList<Double> xtimeList = new ArrayList<>();
        ArrayList<Double> frictionList = new ArrayList<>();
        ArrayList<Double> sigmaMoyList = new ArrayList<>();
        ArrayList<Double> rollingTorqueList = new ArrayList<>();

        ArrayList<Double> xtimeValue = new ArrayList<>();
        ArrayList<Double> frictionValue= new ArrayList<>();
        ArrayList<Double> sigmaMoyValue= new ArrayList<>();
        ArrayList<Double> rollingTorqueValue= new ArrayList<>();

        ArrayList<Double> xtimeListMean = new ArrayList<>();
        ArrayList<Double> frictionListMean = new ArrayList<>();
        ArrayList<Double> sigmaMoyListMean= new ArrayList<>();
        ArrayList<Double> rollingTorqueListMean = new ArrayList<>();

        Statement stmt = conn.createStatement();


        String query = String.format("SELECT XTIME,FRICTION,SIGMA_MOY, FROM CSV_OUTPUT_FILE WHERE STAND_ID = '%s' AND MATID = %d", Stand_ID, mat_id);

        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            double xtime = rs.getDouble("XTIME");

            double friction = rs.getDouble("FRICTION");
            double sigmaMoy = rs.getDouble("SIGMA_MOY");

            xtimeList.add(xtime);
            frictionList.add(friction);
            sigmaMoyList.add(sigmaMoy);
        }

        String query2 = String.format("Select WORK_ROLL_SPEED FROM FILE_FORMAT WHERE STAND_ID = '%s' AND MATID = %d", Stand_ID, mat_id);

        ResultSet rs2 = stmt.executeQuery(query2);

        while (rs2.next()) {

            double rollingTorque = rs2.getDouble("WORK_ROLL_SPEED");

            rollingTorqueList.add(rollingTorque);

        }

    System.out.println("XTIME List: " + xtimeList.size() +"2 "+xtimeList);
    System.out.println("FRICTION List: " + frictionList.size() +"2 "+frictionList);
    System.out.println("SIGMA_MOY List: " + sigmaMoyList.size() +"4 "+ sigmaMoyList);
    System.out.println("Rool speed List: " + rollingTorqueList.size() +"2 "+ rollingTorqueList);

        ArrayList<Double> timeList = new ArrayList<>();
        ArrayList<Double> valueList = new ArrayList<>();


        int Time = computeTime;
// Remplir les listes avec des valeurs de temps et de mesure
        for (int i = 0; i < xtimeList.size()-Time; i += Time) {
            xtimeValue.add(xtimeList.get(i));
            frictionValue.add(frictionList.get(i));
            sigmaMoyValue.add(sigmaMoyList.get(i));
            rollingTorqueValue.add(rollingTorqueList.get(i));
        }

        // définir la moyenne
        int sublistSize = 5;

        for (int i = 0; i < xtimeValue.size(); i += sublistSize) {
            List<Double> xtimeSubList = xtimeValue.subList(i, Math.min(i + sublistSize, xtimeValue.size()));
            List<Double> frictionSubList = frictionValue.subList(i, Math.min(i + sublistSize, frictionValue.size()));
            List<Double> sigmaMoySubList = sigmaMoyValue.subList(i, Math.min(i + sublistSize, sigmaMoyValue.size()));
            List<Double> rollingTorqueSubList = rollingTorqueValue.subList(i, Math.min(i + sublistSize, rollingTorqueValue.size()));

            double xtimeMean = Collections.max(xtimeSubList);
            double frictionMean = frictionSubList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double sigmaMoyMean = sigmaMoySubList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double rollingTorqueMean = rollingTorqueSubList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            xtimeListMean.add(xtimeMean);
            frictionListMean.add(frictionMean);
            sigmaMoyListMean.add(sigmaMoyMean);
            rollingTorqueListMean.add(rollingTorqueMean);
        }
        System.out.println("XTIME Value " + xtimeListMean.size() +"2 "+xtimeValue);
        System.out.println("XTIME List Mean: " + xtimeListMean.size() +"2 "+xtimeListMean);
        System.out.println("FRICTION List Mean: " + frictionListMean.size() +"2 "+frictionListMean);
        System.out.println("SIGMA_MOY List MEAN: " + sigmaMoyListMean.size() +"4 "+ sigmaMoyListMean);
        System.out.println("Roll speed List mean: " + rollingTorqueListMean.size() +"2 "+ rollingTorqueListMean);

        resultat.add(xtimeListMean);
        resultat.add(frictionListMean);
        resultat.add(sigmaMoyListMean);
        resultat.add(rollingTorqueValue);

        return resultat;

    }

}

