package com.example.demo13;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    // Constants for scene FXML files
    private static final String LOGIN_SCENE_FXML = "scondnavigation/Login.fxml";
    private static final String REGISTER_SCENE_FXML = "scondnavigation/register.fxml";
    private static final String WELCOME_SCENE_FXML = "scondnavigation/welcome.fxml";
    private static final String WELCOME_MATCHES_FXML = "1navigation/matches.fxml";
    private static final String WELCOME_NOTIFICATION_FXML = "1navigation/notification.fxml";
    private static final String WELCOME_PROFILE_FXML = "1navigation/profile.fxml";
    private static final String WELCOME_MESSAGES_FXML = "1navigation/Messages.fxml";
    private static final String WELCOME_UPLOADING_IMAGE_FXML = "1navigation/uploadimage.fxml";

    private static final String CHATTING_FXML="1navigation/chattinginterface.fxml";
    private static SceneManager instance;
    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Existing scene loading methods
    public void showLoginScene(int userId) {
        loadScene(LOGIN_SCENE_FXML, userId);
    }

    public void showRegisterScene(int userId) {
        loadScene(REGISTER_SCENE_FXML, userId);
    }

    public void showMatchesScene(int userId) {
        loadScene(WELCOME_MATCHES_FXML, userId);
    }

    public void showProfileScene(int userId) {
        loadScene(WELCOME_PROFILE_FXML, userId);

    }
    public void showchattingScene(int userId,int friendsshpid ) {
        loadChattingScene(CHATTING_FXML, userId,friendsshpid);

    }



    public void showMessagesScene(int userId) {


        loadScene(WELCOME_MESSAGES_FXML, userId);
    }

    public void showNotificationScene(int userId) {
        loadScene(WELCOME_NOTIFICATION_FXML, userId);
    }

    public void showUploadImageScene(int userId) {
        loadScene(WELCOME_UPLOADING_IMAGE_FXML, userId);
    }

    public void showWelcomeScene(int userId) {
        loadScene(WELCOME_SCENE_FXML, userId);
    }

    private void loadScene(String fxmlFile, int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            if (loader.getController() instanceof BaseController) {
                BaseController controller = (BaseController) loader.getController();
                controller.setPrimaryStage(primaryStage);



                if (controller instanceof profilecontaoller) { // Check if the controller is an instance of profilecontaoller
                    profilecontaoller profileController = (profilecontaoller) controller;
                    profileController.setUserId(userId);

                    profileController.loadUserData();
                    System.out.println("loaded");
                    System.out.println("User ID set in profilecontaoller: " + userId);

                }
                if (controller instanceof ImageController) {
                    ImageController img = (ImageController) controller;
                        img.setUserId(userId);
                        System.out.println("User ID set in ImageController: " + userId);
                    System.out.println("User ID set in ImageController: " + userId);


                }
                if (controller instanceof matchescontroller) {
                    matchescontroller match = (matchescontroller) controller;
                    match.setUserId(userId);
                    System.out.println("User ID set in matchescontroller: " + userId);


                }
                if (controller instanceof messagescontroller) {
                    messagescontroller msg = (messagescontroller) controller;
                    msg.setUserId(userId);
                    System.out.println("User ID set in messagescontroller: " + userId);


                }
                if (controller instanceof notificationcontroller) {
                    notificationcontroller not = (notificationcontroller) controller;
                    not.setUserId(userId);
                    System.out.println("User ID set in notificationcontroller: " + userId);
                }


            }

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception gracefully, e.g., show an error dialog
        }
    }
    private void loadChattingScene(String fxmlFile, int userId, int friendsshpid) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            if (loader.getController() instanceof BaseController) {
                BaseController controller = (BaseController) loader.getController();
                controller.setPrimaryStage(primaryStage);

                if (controller instanceof ChatClientController) {
                    ChatClientController chat = (ChatClientController) controller;
                    chat.setUserId(userId);
                    chat.setFriendUserId(friendsshpid);
                    System.out.println("User ID set in ChatClientController: " + userId);
                    System.out.println("Friend's HPID set in ChatClientController: " + friendsshpid);
                }
            }

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception gracefully, e.g., show an error dialog
        }
    }


    public static SceneManager getInstance(Stage primaryStage) {
        if (instance == null) {
            instance = new SceneManager(primaryStage);
        }
        return instance;
    }
}
