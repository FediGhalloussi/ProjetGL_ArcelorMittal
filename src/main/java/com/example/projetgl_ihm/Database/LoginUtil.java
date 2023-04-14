package com.example.projetgl_ihm.Database;

import com.example.projetgl_ihm.Models.Employee;
import com.example.projetgl_ihm.Models.Engineer;
import com.example.projetgl_ihm.Models.Worker;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginUtil {

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
                String username = rs.getString("username");
                String password = rs.getString("password");
                String grade = rs.getString("grade");

                if (grade.equals("Worker")) {
                    users.add(new Worker(username, password));
                } else if (grade.equals("Engineer")) {
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
            statement = conn.prepareStatement("SELECT * FROM Users WHERE Username = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                // User does not exist, insert new user into database
                statement = conn.prepareStatement("INSERT INTO Users (Username, Password, Grade) VALUES (?, ?, ?)");
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
}
