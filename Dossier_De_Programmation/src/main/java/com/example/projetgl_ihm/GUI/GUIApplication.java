package com.example.projetgl_ihm.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1120, 630);
        Image favicon = new Image("C:\\Users\\Amine\\IdeaProjects\\ProjetGL_finale\\src\\main\\resources\\com\\example\\projetgl_ihm\\GUI\\tools_drawing_icon_251433.png");
        stage.getIcons().add(favicon);
        stage.setTitle("Projet GL");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}