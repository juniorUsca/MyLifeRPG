package com.debugcc.myliferpg.Models;
public class User {

    public static final String FACEBOOK_PROVIDER = "Facebook";
    public static final String GOOGLE_PROVIDER = "Google";

    String id;
    String name;
    String email;
    String urlProfilePicture;
    String provider;

    public User() {
        id = "";
        name = "";
        email = "";
        urlProfilePicture = "";
        provider = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlProfilePicture() {
        return urlProfilePicture;
    }

    public void setUrlProfilePicture(String urlProfilePicture) {
        this.urlProfilePicture = urlProfilePicture;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", urlProfilePicture='" + urlProfilePicture + '\'' +
                ", provider='" + provider + '\'' +
                '}';
    }
}
