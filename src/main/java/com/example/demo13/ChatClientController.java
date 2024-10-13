package com.example.demo13;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatClientController implements BaseController {

    @FXML
    public TextArea messageArea;

    @FXML
    private TextField inputField;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private int senderID;  // Sender's ID
    private int receiverID;  // Receiver's ID
    private int friendshipID;  // FriendshipID

    private List<String> messages = new ArrayList<>();
    private int userId;
    private Stage primaryStage;
    public void setFriendUserId(int friendshipID) {
        this.friendshipID = friendshipID;
        displaySavedMessages();  // Call the method when friendshipID is set

        System.out.println(" chat FriendshipID set to: " + friendshipID); // Debug statement

    }

    @FXML
    public void initialize() {

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        messages.add(message + "\n");
                        String finalMessage = message;
                        Platform.runLater(() -> {
                            messageArea.appendText(finalMessage + "\n");
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMessage() {
        String message = inputField.getText();
        System.out.println("zzz: " + friendshipID); // Debug statement

        if (userId != 0) {
            String fullMessage = message;

            out.println(fullMessage);
            messages.add(fullMessage + "\n");
            Platform.runLater(() -> {
                messageArea.appendText(fullMessage + "\n");
            });

            saveMessage(friendshipID, message);

            inputField.clear();
        } else {
            System.out.println("User ID is not set!");
        }
    }

    private void saveMessage(int friendshipID, String messageContent) {
        System.out.println("FriendshipID: " + friendshipID);

        DatabaseManager.saveMessage(friendshipID, messageContent);
    }

    private void displaySavedMessages() {
        if (friendshipID != 0) {
            System.out.println("zzz: " + friendshipID); // Debug statement

            List<String> savedMessages = DatabaseManager.getChatMessages(friendshipID);
            for (String message : savedMessages) {
                messageArea.appendText(message + "\n");
            }
        } else {
            System.out.println("FriendshipID is not set!");
        }
    }


    public void shutdown() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void setUserId(int userId) {
        this.userId = userId;
        displaySavedMessages();  // Call the method when friendshipID is set

        System.out.println("chat userid set to: " + userId); // Debug statement

    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

    public void goBack(MouseEvent mouseEvent) {
        SceneManager.getInstance(primaryStage).showProfileScene(userId);

    }
}
