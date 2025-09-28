package com.ussd.mtn.service;

import com.ussd.mtn.model.*;
import com.ussd.mtn.repository.UssdMenuRepository;
import com.ussd.mtn.repository.UssdSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class UssdService {

    @Autowired
    private UssdMenuRepository menuRepository;

    @Autowired
    private UssdSessionRepository sessionRepository;

    private static final String ROOT_MENU_ID = "ROOT";

    public UssdResponse processUssdRequest(String sessionId, String phoneNumber, String text) {
        try {
            UssdSession session = getOrCreateSession(sessionId, phoneNumber);

            if (text == null || text.isEmpty()) {
                return showMenu(ROOT_MENU_ID, session);
            } else {
                return handleUserInput(text, session);
            }
        } catch (Exception e) {
            return new UssdResponse("END An error occurred. Please try again.", true);
        }
    }

    private UssdSession getOrCreateSession(String sessionId, String phoneNumber) {
        Optional<UssdSession> existingSession = sessionRepository.findBySessionIdAndIsActiveTrue(sessionId);
        if (existingSession.isPresent()) {
            return existingSession.get();
        } else {
            UssdSession newSession = new UssdSession(sessionId, phoneNumber, ROOT_MENU_ID);
            return sessionRepository.save(newSession);
        }
    }

    private UssdResponse showMenu(String menuId, UssdSession session) {
        Optional<UssdMenu> menuOpt = menuRepository.findByMenuId(menuId);
        if (menuOpt.isEmpty()) {
            return new UssdResponse("END Menu not found.", true);
        }

        UssdMenu menu = menuOpt.get();
        session.setCurrentMenuId(menuId);
        session.setUpdatedAt(new Date());
        sessionRepository.save(session);

        return new UssdResponse(buildMenuMessage(menu), false);
    }

    private String buildMenuMessage(UssdMenu menu) {
        StringBuilder message = new StringBuilder(menu.getMessage()).append("\n");

        if (menu.getOptions() != null) {
            int optionNumber = 1;
            for (Map.Entry<String, UssdOption> entry : menu.getOptions().entrySet()) {
                UssdOption option = entry.getValue();
                message.append(optionNumber).append(". ").append(option.getOptionText()).append("\n");
                optionNumber++;
            }
        }

        return message.toString();
    }

    private UssdResponse handleUserInput(String text, UssdSession session) {
        String currentMenuId = session.getCurrentMenuId();
        Optional<UssdMenu> currentMenuOpt = menuRepository.findByMenuId(currentMenuId);

        if (currentMenuOpt.isEmpty()) {
            return new UssdResponse("END Invalid menu.", true);
        }

        UssdMenu currentMenu = currentMenuOpt.get();
        String[] inputParts = text.split("\\*");
        String lastInput = inputParts[inputParts.length - 1];

        // Handle option selection
        if (currentMenu.getOptions() != null && !currentMenu.getOptions().isEmpty()) {
            int optionIndex;
            try {
                optionIndex = Integer.parseInt(lastInput) - 1;
            } catch (NumberFormatException e) {
                return new UssdResponse("CON Invalid option. Please try again:\n" +
                        buildMenuMessage(currentMenu), false);
            }

            UssdOption[] optionsArray = currentMenu.getOptions().values().toArray(new UssdOption[0]);
            if (optionIndex < 0 || optionIndex >= optionsArray.length) {
                return new UssdResponse("CON Invalid option. Please try again:\n" +
                        buildMenuMessage(currentMenu), false);
            }

            UssdOption selectedOption = optionsArray[optionIndex];

            if (selectedOption.isEndOption()) {
                session.setActive(false);
                sessionRepository.save(session);
                return new UssdResponse("END " + selectedOption.getResponseMessage(), true);
            } else if (selectedOption.getNextMenuId() != null) {
                return showMenu(selectedOption.getNextMenuId(), session);
            } else {
                return new UssdResponse("END " + selectedOption.getResponseMessage(), true);
            }
        }

        return new UssdResponse("END No options available.", true);
    }

    public UssdMenu createMenu(String menuId, String message, Map<String, UssdOption> options, String menuType,
            String createdBy) {
        if (menuRepository.existsByMenuId(menuId)) {
            throw new RuntimeException("Menu with ID " + menuId + " already exists");
        }

        UssdMenu menu = new UssdMenu(menuId, message, options, menuType);
        menu.setCreatedBy(createdBy);
        menu.setCreatedAt(new Date().toString());

        return menuRepository.save(menu);
    }

    public UssdMenu updateMenu(String menuId, String message, Map<String, UssdOption> options, String menuType) {
        Optional<UssdMenu> existingMenu = menuRepository.findByMenuId(menuId);
        if (existingMenu.isEmpty()) {
            throw new RuntimeException("Menu with ID " + menuId + " not found");
        }

        UssdMenu menu = existingMenu.get();
        if (message != null)
            menu.setMessage(message);
        if (options != null)
            menu.setOptions(options);
        if (menuType != null)
            menu.setMenuType(menuType);

        return menuRepository.save(menu);
    }

    public void deleteMenu(String menuId) {
        Optional<UssdMenu> menu = menuRepository.findByMenuId(menuId);
        menu.ifPresent(menuRepository::delete);
    }

    public Optional<UssdMenu> getMenu(String menuId) {
        return menuRepository.findByMenuId(menuId);
    }
}