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

import java.sql.SQLException;
import java.util.List;

public class notificationcontroller implements BaseController {
    private static final double IMAGE_WIDTH = 68.0;
    private static final double IMAGE_HEIGHT = 51.0;
    private static final double NAME_LABEL_WIDTH = 150;
    private static final double BUTTON_WIDTH = 120.0;
    private static final double BUTTON_LAYOUT_X = 450.0;
    public AnchorPane slidingPane;
    public VBox userVBox;
    private Stage primaryStage;
    private boolean isPaneVisible = false;
    int userId;
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
        List<UserInfo> users = DatabaseManager.getSenderInfoByReceiverId(userId);

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

            Button acceptRequestButton = createAcceptRequestButton(user.getId());
            Button denyRequestButton = createDenyRequestButton(user.getId());

            HBox userRow = new HBox(imageView, nameLabel2, nameLabel, acceptRequestButton,denyRequestButton);
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

    private Button createDenyRequestButton(int senderId) {
        Button denyRequestButton = new Button("Deny");
        setupButtondeny(denyRequestButton, senderId);

        denyRequestButton.setOnAction(event -> {
            boolean success = DatabaseManager.denyFriendRequest(userId, senderId);

            if (success) {
                System.out.println("Friend request denied successfully.");
                // Remove the corresponding HBox from userVBox
                removeUserRow(senderId);
            } else {
                System.out.println("Failed to deny friend request.");
            }
        });

        return denyRequestButton;
    }

    private Button createAcceptRequestButton(int senderId) {
        Button acceptRequestButton = new Button("Accept");
        setupButtonaccept(acceptRequestButton, senderId);

        acceptRequestButton.setOnAction(event -> {
            try {
                boolean success = DatabaseManager.acceptFriendRequest(userId, senderId);

                if (success) {

                    int friendshipId = DatabaseManager.getFriendshipId(userId, senderId);

                    boolean tableCreated = DatabaseManager.createChatTable(friendshipId);
                    if (tableCreated) {
                        System.out.println("Chat table created successfully.");
                    } else {
                        System.out.println("Failed to create chat table.");
                    }

                    // Remove the corresponding HBox from userVBox
                    removeUserRow(senderId);
                } else {
                    System.out.println("Failed to accept friend request.");
                }
            } catch (Exception ex) {
                // Handle other exceptions
                System.out.println("An error occurred: " + ex.getMessage());
                ex.printStackTrace();
            }
        });


        return acceptRequestButton;
    }

    private void removeUserRow(int senderId) {
        for (int i = 0; i < userVBox.getChildren().size(); i++) {
            HBox hbox = (HBox) userVBox.getChildren().get(i);
            Button denyButton = (Button) hbox.getChildren().get(hbox.getChildren().size() - 1);
            Button acceptButton = (Button) hbox.getChildren().get(hbox.getChildren().size() - 2);

            int currentSenderId = (int) denyButton.getProperties().get("senderId");
            if (currentSenderId == senderId) {
                userVBox.getChildren().remove(i);
                break;
            }
        }
    }


    private void setupButtonaccept(Button button, int senderId) {
        button.setPrefHeight(IMAGE_HEIGHT);
        button.setPrefWidth(BUTTON_WIDTH);
        button.setLayoutX(BUTTON_LAYOUT_X);
        button.getProperties().put("senderId", senderId);
        button.setStyle(" -fx-background-color: #32a852;-fx-background-radius: 60; -fx-text-fill: WHITE;");
    }
    private void setupButtondeny(Button button, int senderId) {
        button.setPrefHeight(IMAGE_HEIGHT);
        button.setPrefWidth(BUTTON_WIDTH);
        button.setLayoutX(BUTTON_LAYOUT_X);
        button.getProperties().put("senderId", senderId);
        button.setStyle(" -fx-background-color: #f2494e;-fx-background-radius: 60; -fx-text-fill: WHITE;");
    }


}



