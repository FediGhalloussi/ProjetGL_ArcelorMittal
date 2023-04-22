module com.example.projetgl_ihm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.rmi;


    exports com.example.projetgl_ihm.utils;
    exports com.example.projetgl_ihm.GUI;
    opens com.example.projetgl_ihm.GUI to javafx.fxml;
}