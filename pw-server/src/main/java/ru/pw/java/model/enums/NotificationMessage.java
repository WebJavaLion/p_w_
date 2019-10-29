package ru.pw.java.model.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lev_S
 */

public enum NotificationMessage {
    MAIL("Hello drug this is time to repeat your word-group with name %s"),
    WEB("Hello drug this is time to repeat your word-group with name %s"),
    TELEGRAM("Hello drug this is time to repeat your word-group with name %s");

    @Getter
    private String message;

    NotificationMessage(String name) {
        this.message = message;
    }
}
