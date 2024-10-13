package com.example.demo13;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;



public class LoginController implements BaseController {
int id;



    @FXML
    public Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;
    private Stage primaryStage;  // Reference to the primary stage
    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

    @FXML
    protected int onLoginButtonClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        System.out.println("Login successful!"+username);
        System.out.println("Login successful!"+password);
        if (DatabaseManager.authenticate(username, password)) {
            statusLabel.setText("Login successful!");
            System.out.println("Login successful!");
id=DatabaseManager.getUserId(username,password);

            SceneManager.getInstance(primaryStage).showProfileScene(id);


        } else {

            statusLabel.setText("Invalid username or password.");

        }
        return id;
    }

    public void goBack(MouseEvent mouseEvent) {
        SceneManager.getInstance(primaryStage).showWelcomeScene(id);
    }


}
