package com.example.asfinal.model;

public class Images {
    private String id;
    private String user_id;
    private String images;

    public Images() {
    }

    public Images(String id, String user_id, String images) {
        this.id = id;
        this.user_id = user_id;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
