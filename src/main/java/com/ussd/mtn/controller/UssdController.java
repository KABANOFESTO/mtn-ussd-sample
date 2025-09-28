package com.ussd.mtn.controller;

import com.ussd.mtn.dto.UssdRequest;
import com.ussd.mtn.model.UssdResponse;
import com.ussd.mtn.service.UssdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ussd")
public class UssdController {

    @Autowired
    private UssdService ussdService;

    @PostMapping("/process")
    public ResponseEntity<String> processUssd(@RequestBody UssdRequest request) {
        UssdResponse response = ussdService.processUssdRequest(
                request.getSessionId(),
                request.getPhoneNumber(),
                request.getText());

        String ussdResponse = response.isShouldEnd() ? response.getResponse() : "CON " + response.getResponse();

        return ResponseEntity.ok(ussdResponse);
    }

    // Admin endpoints for managing menus
    @PostMapping("/admin/menu")
    public ResponseEntity<?> createMenu(@RequestBody MenuCreationRequest menuRequest) {
        try {
            return ResponseEntity.ok(ussdService.createMenu(
                    menuRequest.getMenuId(),
                    menuRequest.getMessage(),
                    menuRequest.getOptions(),
                    menuRequest.getMenuType(),
                    menuRequest.getCreatedBy()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/admin/menu/{menuId}")
    public ResponseEntity<?> updateMenu(@PathVariable String menuId, @RequestBody MenuUpdateRequest menuRequest) {
        try {
            return ResponseEntity.ok(ussdService.updateMenu(
                    menuId,
                    menuRequest.getMessage(),
                    menuRequest.getOptions(),
                    menuRequest.getMenuType()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/menu/{menuId}")
    public ResponseEntity<?> deleteMenu(@PathVariable String menuId) {
        try {
            ussdService.deleteMenu(menuId);
            return ResponseEntity.ok("Menu deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/admin/menu/{menuId}")
    public ResponseEntity<?> getMenu(@PathVariable String menuId) {
        try {
            return ussdService.getMenu(menuId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DTO classes for admin requests
    public static class MenuCreationRequest {
        private String menuId;
        private String message;
        private java.util.Map<String, com.ussd.mtn.model.UssdOption> options;
        private String menuType;
        private String createdBy;

        // Getters and setters
        public String getMenuId() {
            return menuId;
        }

        public void setMenuId(String menuId) {
            this.menuId = menuId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public java.util.Map<String, com.ussd.mtn.model.UssdOption> getOptions() {
            return options;
        }

        public void setOptions(java.util.Map<String, com.ussd.mtn.model.UssdOption> options) {
            this.options = options;
        }

        public String getMenuType() {
            return menuType;
        }

        public void setMenuType(String menuType) {
            this.menuType = menuType;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }
    }

    public static class MenuUpdateRequest {
        private String message;
        private java.util.Map<String, com.ussd.mtn.model.UssdOption> options;
        private String menuType;

        // Getters and setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public java.util.Map<String, com.ussd.mtn.model.UssdOption> getOptions() {
            return options;
        }

        public void setOptions(java.util.Map<String, com.ussd.mtn.model.UssdOption> options) {
            this.options = options;
        }

        public String getMenuType() {
            return menuType;
        }

        public void setMenuType(String menuType) {
            this.menuType = menuType;
        }
    }
}
