package com.ussd.mtn.dto;

public class UssdRequest {
    private String sessionId;
    private String phoneNumber;
    private String serviceCode;
    private String text;

    public UssdRequest() {
    }

    public UssdRequest(String sessionId, String phoneNumber, String serviceCode, String text) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.serviceCode = serviceCode;
        this.text = text;
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

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
