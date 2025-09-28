package com.ussd.mtn.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Document(collection = "ussd_menus")
public class UssdMenu {
    @Id
    private String id;
    private String menuId;
    private String message;
    private Map<String, UssdOption> options;
    private String menuType; 
    private String createdBy;
    private String createdAt;

    // Constructors, getters, and setters
    public UssdMenu() {}

    public UssdMenu(String menuId, String message, Map<String, UssdOption> options, String menuType) {
        this.menuId = menuId;
        this.message = message;
        this.options = options;
        this.menuType = menuType;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Map<String, UssdOption> getOptions() { return options; }
    public void setOptions(Map<String, UssdOption> options) { this.options = options; }
    
    public String getMenuType() { return menuType; }
    public void setMenuType(String menuType) { this.menuType = menuType; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
