package com.example.demo13;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class messagescontroller implements BaseController {
    public AnchorPane slidingPane;
    public Button sendrequest;
    public VBox userVBox;

    private Stage primaryStage;
    private boolean isPaneVisible = false;
    private int userId;

    // Layout constants
    private static final double IMAGE_WIDTH = 68.0;
    private static final double IMAGE_HEIGHT = 51.0;
    private static final double NAME_LABEL_WIDTH = 250.0;
    private static final double BUTTON_WIDTH = 120.0;
    private static final double BUTTON_LAYOUT_X = 450.0;
    public void setUserId(int userId) {
        this.userId=userId;
        populateUserVBox();
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
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
        SceneManager.getInstance(primaryStage).showProfileScene(userId);

    }

    public void goToNotifications(ActionEvent actionEvent) {
        SceneManager.getInstance(primaryStage).showNotificationScene(userId);

    }

    public void goToMatches(ActionEvent actionEvent) {
        SceneManager.getInstance(primaryStage).showMatchesScene(userId);

    }
    private void populateUserVBox() {
        // Retrieve all users from the database
        List<UserInfo> users = DatabaseManager.getAcceptedFriends(userId);

        // Default image
        Image defaultImage = new Image("com/example/demo13/1navigation/images/deafultprof.jpg");

        // Populate the VBox with user information
        for (UserInfo user : users) {
            ImageView imageView;

            if (user.getImage() != null) {
                imageView = new ImageView(user.getImage());
            } else {
                imageView = new ImageView(defaultImage);
            }

            imageView.setFitHeight(IMAGE_HEIGHT);
            imageView.setFitWidth(IMAGE_WIDTH);

            Label nameLabel = new Label(user.getUsername());
            nameLabel.setPrefHeight(IMAGE_HEIGHT);
            nameLabel.setPrefWidth(NAME_LABEL_WIDTH);

            Label nameLabel2 = new Label();
            nameLabel2.setPrefHeight(IMAGE_HEIGHT);
            nameLabel2.setPrefWidth(50.0); // You can adjust this width as needed

            Button sendRequestButton = createSendRequestButton(user.getId());

            HBox userRow = new HBox(imageView, nameLabel2, nameLabel, sendRequestButton);
            userRow.setSpacing(10);

            userRow.setOnMouseClicked(event -> {
                // Reset style for all HBoxes
                for (int i = 0; i < userVBox.getChildren().size(); i++) {
                    HBox hbox = (HBox) userVBox.getChildren().get(i);
                    hbox.setStyle("-fx-border-color: transparent;");
                }

                // Apply selected style to clicked HBox
                userRow.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
            });

            userVBox.getChildren().add(userRow);
        }
    }

    private Button createSendRequestButton(int receiverId) {
        Button sendRequestButton = new Button("got to chat");
        sendRequestButton.setPrefHeight(IMAGE_HEIGHT);
        sendRequestButton.setPrefWidth(BUTTON_WIDTH);
        sendRequestButton.setLayoutX(BUTTON_LAYOUT_X);
        sendRequestButton.setStyle("-fx-background-color: #000000; -fx-background-radius: 60; -fx-text-fill: WHITE;");
        sendRequestButton.getProperties().put("receiverId", receiverId);

        sendRequestButton.setOnAction(event -> {
            int receiverIdFromButton = (int) sendRequestButton.getProperties().get("receiverId");
            int friendsshpid=DatabaseManager.getFriendshipId(receiverIdFromButton,userId);
            System.out.println("friendsshpid"+friendsshpid);

            SceneManager.getInstance(primaryStage).showchattingScene(userId,friendsshpid);



        });

        return sendRequestButton;
    }
}



