package com.sadek.go4lunch.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.sadek.go4lunch.BuildConfig;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String name;
    private String address;
    private int distance;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private int numberOfWorkmates;
    private double rating = 0.0;
    private String openingTime;
    private String phone;
    private String website;
    private String placeId;

    public Restaurant() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNumberOfWorkmates() {
        return numberOfWorkmates;
    }

    public void setNumberOfWorkmates(int numberOfWorkmates) {
        this.numberOfWorkmates = numberOfWorkmates;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
            this.rating = rating;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    // Create a restaurant and add data from 2 sources
    @NonNull
    @Contract("_, _ -> param2")
    public static Restaurant  addDataFromNearByPlace(@NonNull NearByPlace.Result result) {
        String photoBase = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&maxheight=300&photoreference=";

        Restaurant restaurant = new Restaurant();
        restaurant.setName(result.getName());
        restaurant.setAddress(result.getVicinity());
        restaurant.setPlaceId(result.getPlaceId());
        restaurant.setLatitude(result.getGeometry().getLocation().getLat());
        restaurant.setLongitude(result.getGeometry().getLocation().getLng());

        if(result.getOpeningHours() != null) {
            Log.i("time","result: " + result.getOpeningHours().getOpenNow());
            if(result.getOpeningHours().getOpenNow()) {
                restaurant.setOpeningTime("Open");
            }else {
                restaurant.setOpeningTime("Close");
            }
        }


        if(result.getPhotos() != null) {
            restaurant.setImageUrl(photoBase + result.getPhotos().get(0).getPhotoReference()
            + "&key=" + BuildConfig.GOOGLE_API_KEY);
        }

        return restaurant;
    }

    public void addDataFromNearByPlacesDetails(@NonNull NearByPlacesDetails.Result result) {
        if (result.getRating() != null) {
            this.setRating(result.getRating());
        }
        this.setPhone(result.getFormattedPhoneNumber());
        this.setWebsite(result.getWebsite());
    }
}
