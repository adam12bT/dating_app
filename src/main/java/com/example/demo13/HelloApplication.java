package com.example.demo13;

import javafx.application.Application;

import javafx.stage.Stage;



public class HelloApplication extends Application {
int id=0;
    private SceneManager sceneManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        sceneManager = new SceneManager(primaryStage);
        sceneManager.showWelcomeScene(id);

        primaryStage.setTitle("Welcome");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

