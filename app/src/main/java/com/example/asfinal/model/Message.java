package com.example.asfinal.model;


import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Message {
    private String id;
    private String senderId;
    //    private String receiverId;
    private String content;
    private String images;
    private long timestamp;

    // Constructors, getters and setters

    public Message() {
    }

    public Message(String senderId, String content, String images, long timestamp) {
        this.senderId = senderId;
        this.images = images;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

}


