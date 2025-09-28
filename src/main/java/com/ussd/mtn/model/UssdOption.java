package com.ussd.mtn.model;

public class UssdOption {
    private String optionText;
    private String nextMenuId;
    private String responseMessage;
    private boolean isEndOption;

    // Constructors
    public UssdOption() {}

    public UssdOption(String optionText, String nextMenuId, String responseMessage, boolean isEndOption) {
        this.optionText = optionText;
        this.nextMenuId = nextMenuId;
        this.responseMessage = responseMessage;
        this.isEndOption = isEndOption;
    }

    // Getters and setters
    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }
    
    public String getNextMenuId() { return nextMenuId; }
    public void setNextMenuId(String nextMenuId) { this.nextMenuId = nextMenuId; }
    
    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
    
    public boolean isEndOption() { return isEndOption; }
    public void setEndOption(boolean endOption) { isEndOption = endOption; }
}
