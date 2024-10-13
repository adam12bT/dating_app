package com.example.demo13;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ImageController implements BaseController {
    public Label statusLabel;


    public ImageView imageView;
    int userId = 0;
    private Stage primaryStage;

    public void setUserId(int userId) {
        this.userId = userId;
        loadProfileData();  // Load all user-specific data

    }

    public void goBack(MouseEvent mouseEvent) {

        SceneManager.getInstance(primaryStage).showProfileScene(userId);

    }


    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    private void loadProfileData() {
        //Image image = DatabaseManager.loadImage(userId);

    }


    @FXML
    protected void onUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.png, *.jpg, *.jpeg, *.gif)", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                Image img = new Image(file.toURI().toString());
                imageView.setImage(img);
                storeImage(file, userId);
            } catch (Exception e) {
                statusLabel.setText("Error loading image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


            private void storeImage (File file, int userId){
                boolean result = DatabaseManager.storeImage(file, userId);
                if (result) {
                    statusLabel.setText("Image updated successfully.");
                } else {
                    statusLabel.setText("Failed to update image.");
                }
            }
        }






