package com.example.demo13;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class profilecontaoller implements BaseController {
    @FXML
    public Label emailField;
    public PasswordField oldpasswordField;
    public PasswordField newpasswordField;
    public Button saveButton;
    public Button PhotoButton;
    public ImageView profileImageView;
    public Label statusLabel;

    @FXML
    private AnchorPane slidingPane;
    private boolean isPaneVisible = false;

    private Stage primaryStage;
    private int userId;
    public void setUserId(int userId) {
        this.userId = userId;
        System.err.println("id"+userId);


    }
    @FXML

    private void initialize() {
    }

    void loadUserData() {
        loadEmail();
        loadImage();
    }

    private void loadEmail() {
        if (emailField == null) {
            System.err.println("Email field is not initialized!");
            return;
        }

        String email = DatabaseManager.loadEmail(userId);
        if (email != null) {
            emailField.setText(email);
        } else {
            statusLabel.setText("Failed to load email.");
        }
    }


    private void loadImage() {
        Image image = DatabaseManager.loadImage(userId);
        if (image != null) {
            profileImageView.setImage(image);
            profileImageView.setVisible(true);
        }
    }

    @FXML
    private void onSaveButtonClick() {
        String old_password = oldpasswordField.getText().trim();
        String new_password = newpasswordField.getText().trim();

        if (old_password.isEmpty() || new_password.isEmpty()) {
            statusLabel.setText("Please fill all fields!");
            return;
        }

        if (old_password.equals(new_password)) {
            statusLabel.setText("Your new password needs to be different from your old password!");
            return;
        }

        if (DatabaseManager.updatePassword(userId, old_password, new_password)) {
            statusLabel.setText("Password updated successfully!");
        } else {
            statusLabel.setText("Failed to update password. Please check your old password.");
        }
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

    public void onPhotoButtonClick(ActionEvent actionEvent) {
        SceneManager.getInstance(primaryStage).showUploadImageScene(userId);
    }


    @FXML
    private void toggleSlide() {
        double targetX = isPaneVisible ? -200 : 0;
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.3), slidingPane);
        slide.setToX(targetX);
        slide.play();

        isPaneVisible = !isPaneVisible;
    }




    public void goToMessages(ActionEvent actionEvent) {
        SceneManager.getInstance(primaryStage).showMessagesScene(userId);

    }

    public void goToProfile(ActionEvent actionEvent) {

    }

    public void goToNotifications(ActionEvent actionEvent) {
        SceneManager.getInstance(primaryStage).showNotificationScene(userId);

    }

    public void goToMatches(ActionEvent actionEvent) {
        SceneManager.getInstance(primaryStage).showMatchesScene(userId);

    }
}
