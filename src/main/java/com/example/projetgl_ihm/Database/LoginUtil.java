package com.example.projetgl_ihm.Database;

import com.example.projetgl_ihm.Models.Employee;
import com.example.projetgl_ihm.Models.Engineer;
import com.example.projetgl_ihm.Models.Worker;

import java.sql.*;

public class LoginUtil {

    public static Employee authenticateUser(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Replace with your own database connection details
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "username", "password");

            stmt = conn.prepareStatement("SELECT username, password, grade FROM Users WHERE username=? AND password=?");
            stmt.setString(1, username);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String grade = rs.getString("grade");
                if (grade.equals("Worker")) {
                    return new Worker(username, password);
                } else if (grade.equals("Engineer")) {
                    return new Engineer(username, password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    }}
