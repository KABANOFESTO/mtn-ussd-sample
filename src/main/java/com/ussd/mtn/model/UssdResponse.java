package com.ussd.mtn.model;

public class UssdResponse {
    private String response;
    private boolean shouldEnd;

    public UssdResponse(String response, boolean shouldEnd) {
        this.response = response;
        this.shouldEnd = shouldEnd;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isShouldEnd() {
        return shouldEnd;
    }

    public void setShouldEnd(boolean shouldEnd) {
        this.shouldEnd = shouldEnd;
    }
}
