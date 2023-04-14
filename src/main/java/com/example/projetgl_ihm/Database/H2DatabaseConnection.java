package com.example.projetgl_ihm.Database;

//import orowan.OrowanLauncher;

import java.io.*;
import java.sql.*;

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

    public void ReadCSV_Stand(String path, String tab){

        try {
            // Ouverture du fichier CSV
            File csvFile = new File(path);
            BufferedReader csvReader = new BufferedReader(new FileReader(csvFile));
            csvReader.readLine();
            // Création de la requête SQL pour l'insertion des données
            String insertQuery = "INSERT INTO FILE_FORMAT (LP,MATID,XTIME,XLOC,ENTHICK,EXTHICK,ENTENS,EXTENS,ROLLFORCE,FSLIP,DAIAMETER,ROLLED_LENGTH_FOR_WORK_ROLLS,YOUNGMODULUS,BACKUP_ROLL_DIA,ROLLED_LENGTH_FOR_BACKUP_ROLLS,MU,TORQUE,AVERAGESIGMA,INPUTERROR,LUBWFLUP,LUBWFLLO,LUBOILFLUP,LUBOILFLLO,WORK_ROLL_SPEED) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
                stmt.setInt(2, Integer.parseInt(data[1]));
                stmt.setDouble(3, Double.parseDouble(data[2]));
                stmt.setDouble(4, Double.parseDouble(data[3]));
                stmt.setDouble(5, Double.parseDouble(data[4]));
                stmt.setDouble(6, Double.parseDouble(data[5]));
                stmt.setDouble(7, Double.parseDouble(data[6]));
                stmt.setDouble(8, Double.parseDouble(data[7]));
                stmt.setDouble(9, Double.parseDouble(data[8]));
                stmt.setDouble(10, Double.parseDouble(data[9]));
                stmt.setDouble(11, Double.parseDouble(data[10]));
                stmt.setInt(12, Integer.parseInt(data[11]));
                stmt.setDouble(13, Double.parseDouble(data[12]));
                stmt.setDouble(14, Double.parseDouble(data[13]));
                stmt.setInt(15, Integer.parseInt(data[14]));
                stmt.setDouble(16, Double.parseDouble(data[15]));
                stmt.setDouble(17, Double.parseDouble(data[16]));
                stmt.setDouble(18, Double.parseDouble(data[17]));
                stmt.setDouble(19, Double.parseDouble(data[18]));
                stmt.setDouble(20, Double.parseDouble(data[19]));
                stmt.setDouble(21, Double.parseDouble(data[20]));
                stmt.setDouble(22, Double.parseDouble(data[21]));
                stmt.setDouble(23, Double.parseDouble(data[22]));
                stmt.setDouble(24, Double.parseDouble(data[23]));

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
//    public void ComputeOrowan(int computeTime) throws SQLException, IOException, InterruptedException {
//        int time = 0;
//        try {
//            // Établir une connexion à la base de données
//
//            // Préparer la requête SQL
//            String sql = "SELECT ENTHICK, EXTHICK, ENTENS, EXTENS, DAIAMETER, YOUNGMODULUS, AVERAGESIGMA, MU, ROLLFORCE, FSLIP,XTIME FROM FILE_FORMAT WHERE XTIME> 10 LIMIT 1";
//            PreparedStatement stmt = conn.prepareStatement(sql);
//
//            // Exécuter la requête et récupérer les résultats
//            ResultSet rs = stmt.executeQuery();
//
//            // Écrire les résultats dans un fichier CSV
//            String filename = "resultats.csv";
//            FileWriter writer = new FileWriter(filename);
//            writer.append("Cas\tHe\tHs\tTe\tTs\tDiam_WR\tWRyoung\toffset ini\tmu_ini\tForce\tG\n");
//            // Parcourir les résultats et les afficher
//            while (rs.next()) {
//                double entHick = rs.getDouble("ENTHICK");
//                double extHick = rs.getDouble("EXTHICK");
//                double entEns = rs.getDouble("ENTENS");
//                double extEns = rs.getDouble("EXTENS");
//                double diameter = rs.getDouble("DAIAMETER");
//                double youngModulus = rs.getDouble("YOUNGMODULUS");
//                double averageSigma = rs.getDouble("AVERAGESIGMA");
//                double mu = rs.getDouble("MU");
//                double rollForce = rs.getDouble("ROLLFORCE");
//                double fSlip = rs.getDouble("FSLIP");
//
//                String line = String.format("%d\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\n",1, entHick, extHick,entEns, extEns, diameter, youngModulus, averageSigma, mu, rollForce, fSlip);
//                line =line.replaceAll(",",".");
//                writer.append(line);
//                // Faire quelque chose avec les données récupérées, par exemple les afficher
//                System.out.println("entHick: " + entHick+ "ext" +extHick+ ", entEns: " + entEns + ", extEns: " + extEns + ", diameter: " + diameter + ", youngModulus: " + youngModulus + ", averageSigma: " + averageSigma + ", mu: " + mu + ", rollForce: " + rollForce + ", fSlip: " + fSlip + ", xTime: " );
//            }
//            writer.flush();
//            writer.close();
//            // Fermer la connexion
//            rs.close();
//            stmt.close();
//
//
//        } catch (SQLException e) {
//            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
//        }
//        OrowanLauncher a = new OrowanLauncher();
//        a.launch("resultats.csv");
//        ReadCSV_CSV_Output("tv.txt","\t");
//    }
    public void ReadCSV_CSV_Output(String path, String tab){

        try {
            // Ouverture du fichier CSV
            File csvFile = new File(path);
            BufferedReader csvReader = new BufferedReader(new FileReader(csvFile));
            csvReader.readLine();
            // Création de la requête SQL pour l'insertion des données
            String insertQuery = "INSERT INTO CSV_OUTPUT_FILE (INT,Errors,OffsetYield,Friction,Rolling_Torque,Sigma_Moy,Sigma_Ini,Sigma_Out,Sigma_Max,FORCE_ERROR,SLIP_ERROR,HAS_CONVERGED) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

            // Préparation de la requête SQL avec la connexion à la base de données
            PreparedStatement stmt = conn.prepareStatement(insertQuery);

            // Lecture des données du fichier CSV ligne par ligne
            String row;
            while ((row = csvReader.readLine()) != null) {
                row = row.replaceAll(",", ".");
                String[] data = row.split(tab);
                // Insertion des données dans la base de données
                stmt.setInt(1, Integer.parseInt(data[0]));
                stmt.setString(2, data[1]);
                stmt.setDouble(3, Double.parseDouble(data[2]));
                stmt.setDouble(4, Double.parseDouble(data[3]));
                stmt.setDouble(5, Double.parseDouble(data[4]));
                stmt.setDouble(6, Double.parseDouble(data[5]));
                stmt.setDouble(7, Double.parseDouble(data[6]));
                stmt.setDouble(8, Double.parseDouble(data[7]));
                stmt.setDouble(9, Double.parseDouble(data[8]));
                stmt.setDouble(10, Double.parseDouble(data[9]));
                stmt.setDouble(11, Double.parseDouble(data[10]));
                stmt.setString(12,data[11]);

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

}
