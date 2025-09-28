package com.ussd.mtn.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "ussd_sessions")
public class UssdSession {
    @Id
    private String id;
    private String sessionId;
    private String phoneNumber;
    private String currentMenuId;
    private String previousMenuId;
    private String userInput;
    private Date createdAt;
    private Date updatedAt;
    private boolean isActive;

    public UssdSession() {
    }

    public UssdSession(String sessionId, String phoneNumber, String currentMenuId) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.currentMenuId = currentMenuId;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCurrentMenuId() {
        return currentMenuId;
    }

    public void setCurrentMenuId(String currentMenuId) {
        this.currentMenuId = currentMenuId;
    }

    public String getPreviousMenuId() {
        return previousMenuId;
    }

    public void setPreviousMenuId(String previousMenuId) {
        this.previousMenuId = previousMenuId;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
