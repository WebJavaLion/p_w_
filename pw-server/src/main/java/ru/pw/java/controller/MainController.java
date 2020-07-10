package ru.pw.java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.pw.java.model.pojo.PwUserDetails;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.service.WordService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class MainController {

    private final PwRequestContext requestContext;
    private final WordService wordService;

    @Autowired
    public MainController(PwRequestContext requestContext,
                          WordService wordService) {

        this.requestContext = requestContext;
        this.wordService = wordService;
    }

    @GetMapping
    public String index(Model model) {
        final Optional<PwUserDetails> currentUser = requestContext.getCurrentUser();

        Map<String, Object> attributes = null;

        if (currentUser.isPresent()) {
            attributes = new HashMap<>();
            PwUserDetails user = currentUser.get();

            attributes.put("id", user.getId());
            attributes.put("email", user.getUserEmail());
            attributes.put("telegramId", user.getTelegramUserId());

            model.addAttribute("wordGroups", wordService.getCurrentUserGroups(user.getId()));
        }
        model.addAttribute("userData", attributes);

        return "index";
    }

}
