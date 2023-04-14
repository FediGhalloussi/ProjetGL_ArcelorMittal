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
            System.out.println(rs);

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
    }}
