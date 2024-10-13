package com.example.demo13;

import javafx.scene.image.Image;

public class UserInfo {
    private int id;
    private String username;
    private Image image;

    public UserInfo(int id, String username, Image image) {
        this.id = id;
        this.username = username;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Image getImage() {
        return image;
    }
}
