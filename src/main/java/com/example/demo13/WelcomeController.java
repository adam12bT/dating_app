package com.example.demo13;

import javafx.fxml.FXML;



import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class WelcomeController implements BaseController {
    int id =0;

    public Button signUpButton;
    public Button signInButton;
    private Stage primaryStage;  // Reference to the primary stage

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }



    @FXML
    private void initialize() {
        signInButton.setOnAction(event -> {
            SceneManager sceneManager = new SceneManager(primaryStage);
            sceneManager.showLoginScene(id);
        });

        signUpButton.setOnAction(event -> {
            SceneManager sceneManager = new SceneManager(primaryStage);
            sceneManager.showRegisterScene(id);
        });
    }
}


