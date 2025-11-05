package com.ussd.mtn.service;

import com.ussd.mtn.model.*;
import com.ussd.mtn.repository.UssdMenuRepository;
import com.ussd.mtn.repository.UssdSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UssdService {

    @Autowired
    private UssdMenuRepository menuRepository;

    @Autowired
    private UssdSessionRepository sessionRepository;

    private static final String ROOT_MENU_ID = "ROOT";
    private static final int MAX_OPTIONS_PER_PAGE = 8;
    private static final String NEXT_OPTION = "99";
    private static final String PREV_OPTION = "98";

    public UssdResponse processUssdRequest(String sessionId, String phoneNumber, String text) {
        try {
            UssdSession session = getOrCreateSession(sessionId, phoneNumber);

            if (text == null || text.isEmpty()) {
                session.setCurrentPage(0);
                sessionRepository.save(session);
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
            newSession.setCurrentPage(0);
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

        return new UssdResponse(buildMenuMessage(menu, session), false);
    }

    private String buildMenuMessage(UssdMenu menu, UssdSession session) {
        StringBuilder message = new StringBuilder(menu.getMessage()).append("\n");

        if (menu.getOptions() != null && !menu.getOptions().isEmpty()) {
            // Separate special options (00, 99, 98) from regular options
            Map<String, UssdOption> regularOptions = new LinkedHashMap<>();
            Map<String, UssdOption> specialOptions = new LinkedHashMap<>();

            for (Map.Entry<String, UssdOption> entry : menu.getOptions().entrySet()) {
                String key = entry.getKey();
                if (key.equals("00") || key.equals("99") || key.equals("98")) {
                    specialOptions.put(key, entry.getValue());
                } else {
                    regularOptions.put(key, entry.getValue());
                }
            }

            int currentPage = session.getCurrentPage();
            int totalOptions = regularOptions.size();
            int totalPages = (int) Math.ceil((double) totalOptions / MAX_OPTIONS_PER_PAGE);

            // Get options for current page
            List<Map.Entry<String, UssdOption>> optionsList = new ArrayList<>(regularOptions.entrySet());
            int startIndex = currentPage * MAX_OPTIONS_PER_PAGE;
            int endIndex = Math.min(startIndex + MAX_OPTIONS_PER_PAGE, totalOptions);

            // Display options for current page
            int displayNumber = 1;
            for (int i = startIndex; i < endIndex; i++) {
                Map.Entry<String, UssdOption> entry = optionsList.get(i);
                message.append(displayNumber).append(". ")
                        .append(entry.getValue().getOptionText()).append("\n");
                displayNumber++;
            }

            // Add navigation options
            if (totalPages > 1) {
                if (currentPage < totalPages - 1) {
                    message.append(NEXT_OPTION).append(". Next\n");
                }
                if (currentPage > 0) {
                    message.append(PREV_OPTION).append(". Previous\n");
                }
            }

            // Add special options (always visible)
            for (Map.Entry<String, UssdOption> entry : specialOptions.entrySet()) {
                if (!entry.getKey().equals("99") && !entry.getKey().equals("98")) {
                    message.append(entry.getKey()).append(". ")
                            .append(entry.getValue().getOptionText()).append("\n");
                }
            }

            // Add page indicator if multiple pages
            if (totalPages > 1) {
                message.append("\nPage ").append(currentPage + 1)
                        .append(" of ").append(totalPages);
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

        // Handle pagination
        if (lastInput.equals(NEXT_OPTION)) {
            int currentPage = session.getCurrentPage();
            Map<String, UssdOption> regularOptions = getRegularOptions(currentMenu);
            int totalPages = (int) Math.ceil((double) regularOptions.size() / MAX_OPTIONS_PER_PAGE);

            if (currentPage < totalPages - 1) {
                session.setCurrentPage(currentPage + 1);
                sessionRepository.save(session);
                return showMenu(currentMenuId, session);
            }
        }

        if (lastInput.equals(PREV_OPTION)) {
            int currentPage = session.getCurrentPage();
            if (currentPage > 0) {
                session.setCurrentPage(currentPage - 1);
                sessionRepository.save(session);
                return showMenu(currentMenuId, session);
            }
        }

        // Handle option selection
        if (currentMenu.getOptions() != null && !currentMenu.getOptions().isEmpty()) {
            int optionIndex;
            try {
                optionIndex = Integer.parseInt(lastInput);
            } catch (NumberFormatException e) {
                return new UssdResponse("CON Invalid option. Please try again:\n" +
                        buildMenuMessage(currentMenu, session), false);
            }

            // Check for special options first (00, exit options)
            UssdOption specialOption = currentMenu.getOptions().get(lastInput);
            if (specialOption != null && (lastInput.equals("00") || lastInput.startsWith("0"))) {
                if (specialOption.isEndOption()) {
                    session.setActive(false);
                    sessionRepository.save(session);
                    return new UssdResponse("END " + specialOption.getResponseMessage(), true);
                } else if (specialOption.getNextMenuId() != null) {
                    session.setCurrentPage(0);
                    sessionRepository.save(session);
                    return showMenu(specialOption.getNextMenuId(), session);
                }
            }

            // Handle numbered options (1-8)
            Map<String, UssdOption> regularOptions = getRegularOptions(currentMenu);
            List<Map.Entry<String, UssdOption>> optionsList = new ArrayList<>(regularOptions.entrySet());

            int currentPage = session.getCurrentPage();
            int startIndex = currentPage * MAX_OPTIONS_PER_PAGE;
            int pageOptionIndex = optionIndex - 1;
            int actualIndex = startIndex + pageOptionIndex;

            if (pageOptionIndex < 0 || actualIndex >= optionsList.size()) {
                return new UssdResponse("CON Invalid option. Please try again:\n" +
                        buildMenuMessage(currentMenu, session), false);
            }

            UssdOption selectedOption = optionsList.get(actualIndex).getValue();

            if (selectedOption.isEndOption()) {
                session.setActive(false);
                sessionRepository.save(session);
                return new UssdResponse("END " + selectedOption.getResponseMessage(), true);
            } else if (selectedOption.getNextMenuId() != null) {
                session.setCurrentPage(0);
                sessionRepository.save(session);
                return showMenu(selectedOption.getNextMenuId(), session);
            } else {
                return new UssdResponse("END " + selectedOption.getResponseMessage(), true);
            }
        }

        return new UssdResponse("END No options available.", true);
    }

    private Map<String, UssdOption> getRegularOptions(UssdMenu menu) {
        if (menu.getOptions() == null) {
            return new LinkedHashMap<>();
        }

        return menu.getOptions().entrySet().stream()
                .filter(entry -> !entry.getKey().equals("00") &&
                        !entry.getKey().equals("99") &&
                        !entry.getKey().equals("98"))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
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