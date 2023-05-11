package com.example.asfinal.model;


import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Message {
    private String senderId;
    //    private String receiverId;
    private String content;
    private long timestamp;

    // Constructors, getters and setters

    public Message() {
    }

    public Message(String senderId, String content, long timestamp) {
        this.senderId = senderId;
//        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
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
}


