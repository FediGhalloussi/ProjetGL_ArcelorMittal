/**
 * Cette classe permet de se connecter à une base de données H2 en utilisant le pilote JDBC H2.
 */
package com.example.projetgl_ihm.amine.base_de_donnée;

import com.example.projetgl_ihm.amine.orowan.OrowanLauncher;

import javax.xml.transform.Source;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class H2DatabaseConnection {
    private Connection conn = null;
    private ArrayList<Double> TimeMean = new ArrayList<>();
    private ArrayList<Double> SigmaMean = new ArrayList<>();
    private ArrayList<Double> FrictionMean = new ArrayList<>();
    private ArrayList<Double> SpeedMean = new ArrayList<>();

    /**
     * Constructeur par défaut de la classe H2DatabaseConnection.
     */
    public H2DatabaseConnection(){}

    /**
     * Cette méthode établit une connexion à la base de données H2 en utilisant une URL, un nom d'utilisateur et un mot de passe.
     */
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

    /**
     * Cette méthode ferme la connexion à la base de données H2.
     */
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
    /**
     * Cette méthode permet de lire les données des capteurs pour chaque stand et de les insérer dans une base de données.
     *
     * @param path   Le chemin absolu du fichier stand.
     * @param tab    Le séparateur utilisé dans le fichier CSV.
     * @param stand  L'identifiant du stand dans lequel les données ont été collectées.
     */
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
    /**
     * Cette méthode permet faire appelle à orowan pour un type d'équiement à un stand donné. La méthode extrait les données des capteurs et compute orwan sur ces données toutes les 200 ms les 200 ms.
     *
     * @param mat_id   numéro_equipement.
     * @param stand_id   numéro stand.
     *
     */
    public void ComputeOrowan( String stand_id, int mat_id) throws SQLException, IOException, InterruptedException {
        double time = 0;

        while (true) {

            String formattedDouble = String.format(Locale.US, "%.2f", time);
            boolean requete_reussit = false;
            ArrayList<Double> times = new ArrayList<Double>();
            try {

                // Établir une connexion à la base de données
                String sqlQuery = String.format("SELECT ENTHICK, EXTHICK, ENTENS, EXTENS, DAIAMETER, YOUNGMODULUS, AVERAGESIGMA, MU, ROLLFORCE, FSLIP, XTIME FROM FILE_FORMAT WHERE XTIME > %s AND STAND_ID = '%s' AND MATID = %d LIMIT 1;", time, stand_id, mat_id);
                System.out.println(sqlQuery);
                // Préparer la requête SQL
                PreparedStatement stmt = conn.prepareStatement(sqlQuery);
                // Exécuter la requête et récupérer les résultats
                ResultSet rs = stmt.executeQuery();
                // Écrire les résultats dans un fichier CSV
                String filename = "src/file/orowan/input.csv";

                FileWriter writer = new FileWriter(filename);
                writer.append("Cas\tHe\tHs\tTe\tTs\tDiam_WR\tWRyoung\toffset ini\tmu_ini\tForce\tG\n");
                // Parcourir les résultats et les afficher

                while (rs.next()) {
                    requete_reussit = true;
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

                    String line = String.format("%d\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\n", 1, entHick, extHick, entEns, extEns, diameter, youngModulus, averageSigma, mu, rollForce, fSlip);
                    System.out.println(line);
                    line = line.replaceAll(",", ".");
                    writer.append(line);

                }
                writer.flush();
                writer.close();
                // Fermer la connexion
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
            }
            if(requete_reussit){
                time +=0.2;
            }
            OrowanLauncher a = new OrowanLauncher();
            a.launch("src/file/orowan/input.csv", "src/file/orowan/output.csv");
            ReadCSV_CSV_Output("src/file/orowan/output.csv", "\t", stand_id, mat_id, times);

            Thread.sleep(200);

        }

    }
    /**
     * Cette méthode permet d'insérer les données calculé par OROWAN dans la base de donnée.
     *
     * @param path  chemin d'output d'orowan.
     * @param tab   type de tabulation.
     * @param stand_id numéro de stand
     * @param mat_id numéro équipement
     *
     */
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

    public ArrayList<Double> getTimeMean() {
        return TimeMean;
    }

    public ArrayList<Double> getSigmaMean() {
        return SigmaMean;
    }

    public ArrayList<Double> getFrictionMean() {
        return FrictionMean;
    }

    public ArrayList<Double> getSpeedMean() {
        return SpeedMean;
    }
    /**
     * Cette méthode permet de calculer les moyennes toutes les 1 secondes et les insérer dans la table AVERAGE DE LA BASE DE DONNZ.
     *
     * @param time  temps pour lesquelles faires les moyennes si autre qu'une seconde.
     * @param Stand_ID numéro de stand
     * @param mat_id numéro équipement
     *
     */
    public void Average(String Stand_ID, int mat_id, double time) throws SQLException, InterruptedException {
        System.out.println(time);
        String formattedDouble = String.format(Locale.US, "%.2f", time);


        ArrayList<Double> xtimeList = new ArrayList<>();
        ArrayList<Double> frictionList = new ArrayList<>();
        ArrayList<Double> sigmaMoyList = new ArrayList<>();
        ArrayList<Double> rolspeedList = new ArrayList<>();

        Statement stmt = conn.createStatement();



            xtimeList.clear();
            frictionList.clear();
            sigmaMoyList.clear();
            rolspeedList.clear();


            String query = String.format("SELECT XTIME,FRICTION,SIGMA_MOY, FROM CSV_OUTPUT_FILE WHERE XTIME > %s AND STAND_ID = '%s' AND MATID = %d LIMIT 5",time, Stand_ID, mat_id);

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                double xtime = rs.getDouble("XTIME");

                double friction = rs.getDouble("FRICTION");
                double sigmaMoy = rs.getDouble("SIGMA_MOY");
                time = xtime;
                xtimeList.add(xtime);
                frictionList.add(friction);
                sigmaMoyList.add(sigmaMoy);
            }

            String query2 = String.format("Select WORK_ROLL_SPEED FROM FILE_FORMAT WHERE XTIME > %s AND STAND_ID ='%s' AND MATID = %d  LIMIT 5", time, Stand_ID, mat_id);

            ResultSet rs2 = stmt.executeQuery(query2);

            while (rs2.next()) {

                double rollingSpeed = rs2.getDouble("WORK_ROLL_SPEED");

                rolspeedList.add(rollingSpeed);

            }

            double sum = 0.0;
            double FrictionMean = 0.0;
            if (frictionList.size()>4){
                time +=1;

                for (Double value : frictionList) {
                    sum += value;
                }
                FrictionMean= sum / 5.0;

            }
            sum = 0.0;
            double Sigma_MoyMean = 0.0;
            if(sigmaMoyList.size()>4){
                for (Double value : sigmaMoyList) {
                    sum += value;
                }
                Sigma_MoyMean = sum / 5.0;
            }

            sum = 0.0;
            double rolspeedMean = 0.0;
            if (rolspeedList.size()>4){
                for (Double value : rolspeedList) {
                    sum += value;
                }
                rolspeedMean = sum / 5.0;
            }
            String sql = "INSERT INTO AVERAGE (FRICTION, TIME, STAND_ID, MATID, SIGMA, SPEED) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt_insert = conn.prepareStatement(sql);
            stmt_insert.setDouble(1, FrictionMean);
            stmt_insert.setDouble(2, time);
            stmt_insert.setString(3, Stand_ID);
            stmt_insert.setInt(4, mat_id);
            stmt_insert.setDouble(5, Sigma_MoyMean);
            stmt_insert.setDouble(6, rolspeedMean);

            // Exécuter la requête SQL pour ajouter la nouvelle ligne dans la table "average_friction"
            int rowsAffected = stmt_insert.executeUpdate();



    }
    /**
     * Cette méthode permet d'actualiser la liste des points à afficher toutes les seconde sur l'applicaiton
     *
     *
     * @param StanD_id numéro de stand
     * @param maT_id numéro équipement
     *
     */
    public void getGraphiques(String StanD_id, int maT_id) {
        this.SigmaMean.clear();
        this.SpeedMean.clear();
        this.TimeMean.clear();
        this.FrictionMean.clear();
        try {

            Statement stmt = conn.createStatement();

            String query = String.format("SELECT FRICTION, TIME, STAND_ID, MATID, SIGMA, SPEED FROM AVERAGE WHERE STAND_ID ='%s' AND MATID = %d", StanD_id, maT_id);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                double time = rs.getDouble("TIME");
                double speed = rs.getDouble("SPEED");
                double friction = rs.getDouble("FRICTION");
                double sigma = rs.getDouble("SIGMA");
                // faire quelque chose avec les valeurs récupérées
                this.SigmaMean.add(sigma);
                this.SpeedMean.add(speed);
                this.TimeMean.add(time);
                this.FrictionMean.add(friction);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

