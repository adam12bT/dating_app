package com.example.demo13;

import javafx.scene.image.Image;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hth";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    public static boolean authenticate(String email, String password) {
        String query = "SELECT password FROM Persons WHERE username  = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    return password.equals(storedPassword);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean insertPerson(String username, String email, String password, String confirmPassword, String dateOfBirth) {
        String query = "INSERT INTO Persons (username, email, password, confirmPassword, date_of_birth) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setString(4, confirmPassword);

            // Convert date format from 'DD/MM/YYYY' to 'YYYY-MM-DD'
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date parsedDate = sdf.parse(dateOfBirth);
            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

            statement.setDate(5, sqlDate);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return false;
        }
    }
    public static int getUserId(String username, String password) {
        String query = "SELECT id FROM Persons WHERE username = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if no user found
    }
    public static String loadEmail(int userId) {
        String query = "SELECT email FROM Persons WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("email");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }



    // Method to update password
    public static boolean updatePassword(int userId, String oldPassword, String newPassword) {
        String query = "SELECT password FROM Persons WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    if (oldPassword.equals(storedPassword)) {
                        String updateQuery = "UPDATE Persons SET password = ? WHERE id = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, newPassword);
                            updateStatement.setInt(2, userId);
                            int affectedRows = updateStatement.executeUpdate();
                            return affectedRows == 1;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return false;
    }






    public static Image loadImage(int userId) {
        String query = "SELECT image FROM Persons WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Blob blob = resultSet.getBlob("image");
                    if (blob != null) {
                        try (InputStream is = blob.getBinaryStream()) {
                            return new Image(is);
                        }
                    }
                }
            }
        } catch (SQLException | IOException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }
    public static boolean storeImage(File file, int userId) {
        if (file == null) {
            return false;
        } else {

            String query = "UPDATE Persons SET image = ? WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(query)) {
                InputStream is = new FileInputStream(file);
                ps.setBlob(1, is, file.length());
                ps.setInt(2, userId);
                int result = ps.executeUpdate();
                return result > 0;
            } catch (SQLException | FileNotFoundException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
    public static List<UserInfo> getNonFriendUsers(int userId) {
        List<UserInfo> nonFriendUsers = new ArrayList<>();
        String query = "SELECT p.id, p.username, p.image FROM Persons p WHERE p.id != ? AND p.id NOT IN " +
                "(SELECT fr.receiver_id FROM friendrequests fr WHERE fr.status = 'accepted'  AND fr.requester_id = ? " +
                "UNION " +
                "SELECT fr.requester_id FROM friendrequests fr WHERE fr.status = 'accepted'  AND fr.receiver_id = ?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setInt(2, userId);
            statement.setInt(3, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    Blob blob = resultSet.getBlob("image");

                    Image image = null;
                    if (blob != null) {
                        try (InputStream is = blob.getBinaryStream()) {
                            image = new Image(is);
                        }
                    }

                    nonFriendUsers.add(new UserInfo(id, username, image));
                }
            }
        } catch (SQLException | IOException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return nonFriendUsers;
    }

    public static String getUsernameById(int id) {
        String query = "SELECT username FROM Persons WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error executing query: " + e.getMessage());
        }
        return null;
    }


    public static boolean sendFriendRequest(int requesterId, int receiverId) {
        String query = "INSERT INTO friendrequests (requester_id, receiver_id, status) VALUES (?, ?, 'pending')";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, requesterId);
            statement.setInt(2, receiverId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int getSenderIdByReceiverId(int receiverId) {
        String query = "SELECT requester_id FROM friendrequests WHERE receiver_id = ? AND status = 'pending'";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, receiverId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("requester_id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return -1; // Return -1 if no sender_id found
    }
    public static List<UserInfo> getSenderInfoByReceiverId(int receiverId) {
        List<UserInfo> senderInfoList = new ArrayList<>();
        String query = "SELECT p.id, p.username, p.image FROM Persons p " +
                "INNER JOIN friendrequests fr ON p.id = fr.requester_id " +
                "WHERE fr.receiver_id = ? AND fr.status = 'pending'";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, receiverId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    Blob blob = resultSet.getBlob("image");

                    Image image = null;
                    if (blob != null) {
                        try (InputStream is = blob.getBinaryStream()) {
                            image = new Image(is);
                        }
                    }

                    senderInfoList.add(new UserInfo(id, username, image));
                }
            }
        } catch (SQLException | IOException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return senderInfoList;
    }
    public static boolean denyFriendRequest(int receiverId, int senderId) {
        String query = "UPDATE friendrequests SET status = 'denied' WHERE requester_id = ? AND receiver_id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean acceptFriendRequest(int receiverId, int senderId) {
        String query = "UPDATE friendrequests SET status = 'accepted' WHERE requester_id = ? AND receiver_id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static int getFriendshipId(int requesterId, int receiverId) {
        int friendshipId = -1;  // Default value if no friendship is found
        String query = "SELECT id FROM friendrequests WHERE ((requester_id = ? AND receiver_id = ?) OR (requester_id = ? AND receiver_id = ?)) AND status = 'accepted'";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, requesterId);
            statement.setInt(2, receiverId);
            statement.setInt(3, receiverId);  // Set the third parameter
            statement.setInt(4, requesterId);  // Set the fourth parameter

            System.out.println("Executing query: " + query);  // Log the executed query
            System.out.println("Parameters: requesterId=" + requesterId + ", receiverId=" + receiverId);  // Log the parameters

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    friendshipId = resultSet.getInt("id");
                    System.out.println("Friendship ID found: " + friendshipId);  // Log the retrieved friendship ID
                } else {
                    System.out.println("No matching friendship found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());  // Log the SQL exception message
            e.printStackTrace();  // Log the stack trace
        }

        return friendshipId;
    }


    public static boolean createChatTable(int friendshipID) {
        String tableName = "ChatMessages_" + friendshipID;
        String sql = "CREATE TABLE " + tableName + " (" +
                "MessageID INT AUTO_INCREMENT PRIMARY KEY, " +
                "SenderID INT, " +
                "ReceiverID INT, " +
                "MessageContent TEXT" +
                ")";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int tableCreated = pstmt.executeUpdate();
            if (tableCreated > 0) {
                System.out.println("Table " + tableName + " created successfully!"); // Log table creation success
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<UserInfo> getAcceptedFriends(int userId) {
        List<UserInfo> acceptedFriends = new ArrayList<>();
        String query = "SELECT p.id, p.username, p.image FROM Persons p " +
                "INNER JOIN friendrequests fr ON p.id = fr.requester_id OR p.id = fr.receiver_id " +
                "WHERE (fr.requester_id = ? OR fr.receiver_id = ?) AND fr.status = 'accepted' AND p.id != ?";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setInt(2, userId);
            statement.setInt(3, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    Blob blob = resultSet.getBlob("image");

                    Image image = null;
                    if (blob != null) {
                        try (InputStream is = blob.getBinaryStream()) {
                            image = new Image(is);
                        }
                    }

                    acceptedFriends.add(new UserInfo(id, username, image));
                }
            }
        } catch (SQLException | IOException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return acceptedFriends;
    }
    public static void saveMessage(int friendshipID, String messageContent) {
        String tableName = "ChatMessages_" + friendshipID;
        System.out.println("Saving message to table: " + tableName); // Log table name

        // SQL query for inserting into the MessageContent column
        String sql = "INSERT INTO " + tableName + " (MessageContent) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, messageContent);  // Set the message content for the first parameter

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Message saved successfully!"); // Log successful message insertion
            } else {
                System.out.println("Failed to save message!"); // Log failure to insert message
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving message: " + e.getMessage()); // Log error message
        }
    }




    public static List<String> getChatMessages(int friendshipID) {
        System.out.println("Error retrieving chat messages: " + friendshipID);

        String tableName = "ChatMessages_" + friendshipID;
        String sql = "SELECT MessageContent FROM "+ tableName;

        List<String> messages = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                String messageContent = rs.getString("MessageContent");


                // Format the message for display
                String formattedMessage = messageContent ;

                messages.add(formattedMessage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving chat messages: " + e.getMessage());
        }

        return messages;
    }




}


