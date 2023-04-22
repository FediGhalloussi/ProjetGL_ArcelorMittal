package com.example.projetgl_ihm.utils;

import com.example.projetgl_ihm.models.Employee;
import com.example.projetgl_ihm.models.Engineer;
import com.example.projetgl_ihm.models.Worker;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginUtil {
    /**
     * Vérifie si un utilisateur existe dans la base de données et s'il a les bonnes informations d'identification.
     * Si l'utilisateur est un travailleur, il est renvoyé un objet Worker, sinon, s'il est ingénieur, il est renvoyé un objet Engineer.
     * Si l'utilisateur n'existe pas ou si les informations d'identification sont incorrectes, la méthode renvoie null.
     *
     * @param username le nom d'utilisateur de l'utilisateur à authentifier
     * @param password le mot de passe de l'utilisateur à authentifier
     * @return l'objet Employee correspondant à l'utilisateur authentifié, ou null si l'utilisateur n'est pas trouvé ou si les informations d'identification sont incorrectes.
     */
    public static Employee authenticateUser(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("org.h2.Driver");

            // Replace with your own database connection details
            String url = "jdbc:h2:tcp://localhost/~/test";
            String user = "projetGL";
            String password_db = "afa";
            conn = DriverManager.getConnection(url, user, password_db);


            stmt = conn.prepareStatement("SELECT USERNAME, PASSWORD,GRADE FROM USERS  WHERE USERNAME=? AND PASSWORD=?");
            stmt.setString(1, username);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String grade = rs.getString("grade");
                if (grade.equals("worker")) {
                    return new Worker(username, password);
                } else if (grade.equals("engineer")) {
                    return new Engineer(username, password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    /**
     * Obtient la liste de tous les utilisateurs dans la base de données.
     * Si l'utilisateur est un travailleur, il est ajouté à la liste en tant qu'objet Worker, sinon, s'il est ingénieur, il est ajouté à la liste en tant qu'objet Engineer.
     * La méthode renvoie une liste vide si aucun utilisateur n'est trouvé.
     *
     * @return une liste d'objets Employee correspondant à tous les utilisateurs dans la base de données, ou une liste vide si aucun utilisateur n'est trouvé.
     */
    public static List<Employee> getAllUsers() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Employee> users = new ArrayList<>();

        try {
            Class.forName("org.h2.Driver");

            // Replace with your own database connection details
            String url = "jdbc:h2:tcp://localhost/~/test";
            String user = "projetGL";
            String password_db = "afa";
            conn = DriverManager.getConnection(url, user, password_db);

            stmt = conn.prepareStatement("SELECT USERNAME, PASSWORD,GRADE FROM USERS");
            rs = stmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String password = rs.getString("PASSWORD");
                String grade = rs.getString("GRADE");

                if (grade.equals("worker")) {
                    users.add(new Worker(username, password));
                } else if (grade.equals("engineer")) {
                    users.add(new Engineer(username, password));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return users;
    }
    /**
     * Enregistre un nouvel utilisateur dans la base de données.
     * La méthode vérifie d'abord si l'utilisateur existe déjà dans la base de données et renvoie false si c'est le cas.
     * Si l'utilisateur n'existe pas encore dans la base de données, la méthode insère un nouveau tuple avec le nom d'utilisateur, le mot de passe et le grade de l'utilisateur.
     *
     * @param username le nom d'utilisateur de l'utilisateur à enregistrer
     * @param password le mot de passe de l'utilisateur à enregistrer
     * @param grade    le grade de l'utilisateur à enregistrer
     * @return true si l'utilisateur est enregistré avec succès, false sinon.
     */
    public static boolean registerUser(String username, String password, String grade) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a connection to the database
            Class.forName("org.h2.Driver");

            // Replace with your own database connection details
            String url = "jdbc:h2:tcp://localhost/~/test";
            String user = "projetGL";
            String password_db = "afa";
            conn = DriverManager.getConnection(url, user, password_db);

            // Check if the user already exists in the database
            statement = conn.prepareStatement("SELECT * FROM USERS WHERE USERNAME = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                // User does not exist, insert new user into database
                statement = conn.prepareStatement("INSERT INTO Users (USERNAME, PASSWORD, GRADE) VALUES (?, ?, ?)");
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, grade);

                statement.executeUpdate();

                System.out.println("New user created!");
                return true;
            } else {
                // User already exists
                System.out.println("Username already exists, please choose a different username.");
                return false;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                // Close the database resources
                if (resultSet != null) {
                    resultSet.close();
                }

                if (statement != null) {
                    statement.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    /**
     * Met à jour le grade d'un utilisateur dans la base de données.
     * La méthode récupère d'abord le grade actuel de l'utilisateur dans la base de données.
     * Si le grade actuel est "worker", le grade est mis à jour à "engineer".
     * Si le grade actuel est "engineer", le grade est mis à jour à "worker".
     *
     * @param username le nom d'utilisateur de l'utilisateur dont le grade doit être mis à jour.
     */
    public static void updateUserGrade(String username) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish a connection to the database
            Class.forName("org.h2.Driver");

            // Replace with your own database connection details
            String url = "jdbc:h2:tcp://localhost/~/test";
            String user = "projetGL";
            String password_db = "afa";
            conn = DriverManager.getConnection(url, user, password_db);
            // Retrieve the user's current grade from the database
            statement = conn.prepareStatement("SELECT * FROM USERS WHERE Username = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Update the user's grade
                String currentGrade = resultSet.getString("GRADE");
                String newGrade;

                if (currentGrade.equals("worker")) {
                    newGrade = "engineer";
                } else {
                    newGrade = "worker";
                }

                statement = conn.prepareStatement("UPDATE USERS SET GRADE = ? WHERE USERNAME = ?");
                statement.setString(1, newGrade);
                statement.setString(2, username);

                statement.executeUpdate();

                System.out.println("User grade updated!");
            } else {
                // User not found
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                // Close the database resources
                if (resultSet != null) {
                    resultSet.close();
                }

                if (statement != null) {
                    statement.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**

     Cette méthode permet de rechercher des utilisateurs dans la base de données

     dont les noms d'utilisateur contiennent une chaîne de recherche donnée.

     @param search la chaîne de recherche utilisée pour rechercher les utilisateurs

     @return une liste d'objets Employee contenant les utilisateurs trouvés
     */
    public static List<Employee> searchUsers(String search) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Employee> users = new ArrayList<>();

        try {
            // Establish a connection to the database
            Class.forName("org.h2.Driver");

            // Replace with your own database connection details
            String url = "jdbc:h2:tcp://localhost/~/test";
            String user = "projetGL";
            String password_db = "afa";
            connection = DriverManager.getConnection(url, user, password_db);
            // Retrieve the users whose usernames contain the search string from the database
            statement = connection.prepareStatement("SELECT * FROM Users WHERE Username LIKE ?");
            statement.setString(1, "%" + search + "%");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Create a new Employee object based on the user's grade
                String grade = resultSet.getString("Grade");
                Employee employee;

                if (grade.equals("Worker")) {
                    employee = new Worker(resultSet.getString("username"),resultSet.getString("password"));
                } else {
                    employee = new Engineer(resultSet.getString("username"),resultSet.getString("password"));
                }

                users.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                // Close the database resources
                if (resultSet != null) {
                    resultSet.close();
                }

                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return users;
    }
}
