package ru.pw.java.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pw.java.service.TelegramService;

/**
 * @author Lev_S
 */

@RestController
@RequestMapping(path = "/tg")
public class TelegramController {

    final TelegramService service;

    public TelegramController(TelegramService service) {
        this.service = service;
    }

    @GetMapping(value = "/telegram/auth/code")
    public ResponseEntity<String> getTelegramAuthCode() {
        return ResponseEntity.ok(service.getTelegramResolveLink(true));
    }

    @GetMapping(value = "/telegram/link/code")
    public ResponseEntity<String> getTelegramLinkCode() {
        return ResponseEntity.ok(service.getTelegramResolveLink(false));
    }

    @PostMapping(value = "/token/is/actual")
    public ResponseEntity<Boolean> tokenIsActual(@RequestBody String token) {
        return ResponseEntity.ok(service.tokenIsActual(token));
    }
}
