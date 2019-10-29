package ru.pw.java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pw.java.service.TelegramService;

/**
 * @author Lev_S
 */

@RestController
@RequestMapping(path = "/tg")
public class TelegramController {

    @Autowired
    TelegramService service;

    @GetMapping(value = "/telegram/auth/code")
    public ResponseEntity getTelegramAuthCode() {
        return ResponseEntity.ok(service.getTelegramResolveLink(true));
    }

    @GetMapping(value = "/telegram/link/code")
    public ResponseEntity getTelegramLinkCode() {
        return ResponseEntity.ok(service.getTelegramResolveLink(false));
    }

    @PostMapping(value = "/token/is/actual")
    public ResponseEntity tokenIsActual(@RequestBody String token) {
        return ResponseEntity.ok(service.tokenIsActual(token));
    }
}
