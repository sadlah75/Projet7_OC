package com.sadek.go4lunch.model;

import java.io.Serializable;
import java.util.List;

public class Workmate implements Serializable {
    private String id;
    private String username;
    private String email;
    private String urlImage;
    private Restaurant chosenRestaurant;
    private List<Restaurant> likedRestaurant;
    private boolean notificationsEnabled;

    public Workmate(){}

    public Workmate(String id, String username, String email, String urlImage, Restaurant chosenRestaurant, List<Restaurant> likedRestaurant, boolean notificationsEnabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.urlImage = urlImage;
        this.chosenRestaurant = chosenRestaurant;
        this.likedRestaurant = likedRestaurant;
        this.notificationsEnabled = notificationsEnabled;
    }

    public Workmate(String urlImage, String username) {
        this.urlImage = urlImage;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Restaurant getChosenRestaurant() {
        return chosenRestaurant;
    }

    public void setChosenRestaurant(Restaurant chosenRestaurant) {
        this.chosenRestaurant = chosenRestaurant;
    }

    public List<Restaurant> getLikedRestaurant() {
        return likedRestaurant;
    }

    public void setLikedRestaurant(List<Restaurant> likedRestaurant) {
        this.likedRestaurant = likedRestaurant;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}
