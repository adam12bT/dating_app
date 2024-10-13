package com.example.demo13;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.*;

public class RegisterController implements BaseController {

    public Button registerButton;
    public TextField calendarField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private Blob image;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label statusLabel;

    private Stage primaryStage;  // Reference to the primary stage
    int id =0;


    @FXML
    private void onRegisterButtonClick() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String dateOfBirth = calendarField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dateOfBirth.isEmpty()) {
            statusLabel.setText("Please fill all fields!");
            return;
        }
        if (!isValidEmail(email)) {
            statusLabel.setText("Invalid email format. Email must contain '@'.");
            return;
        }
        if (!isValidDate(dateOfBirth)) {
            statusLabel.setText("Invalid date format. Date must be in DD/MM/YYYY format.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match!");
            return;
        }

        // Insert all the user details into the database using DatabaseManager
        try {
            boolean isSuccess = DatabaseManager.insertPerson(username, email, password, confirmPassword, dateOfBirth);
            if (isSuccess) {
                statusLabel.setText("Registration successful!");
            } else {
                statusLabel.setText("Registration failed!");
            }
        } catch (Exception e) {
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }
    private boolean isValidEmail(String email) {
        return email.contains("@");
    }

    private boolean isValidDate(String date) {
        if (date == null) return false;
        return date.matches("\\d{2}/\\d{2}/\\d{4}");  // Regular expression for DD/MM/YYYY
    }


    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }



    public void goBack(MouseEvent mouseEvent) {
        SceneManager.getInstance(primaryStage).showWelcomeScene(id);
    }
}
