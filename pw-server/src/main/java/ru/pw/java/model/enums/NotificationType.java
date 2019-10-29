package ru.pw.java.model.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lev_S
 */

public enum NotificationType {
    MAIL(1, "Почтовые уведомления"),
    WEB(2, "Web - уведомления"),
    TELEGRAM(3, "Telegram - уведомления");

    @Getter
    private int id;

    @Getter
    private String name;

    NotificationType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static final Map<Integer, NotificationType> TYPE_MAP;

    static {
        Map<Integer, NotificationType> map = new HashMap<>();
        for (NotificationType type : NotificationType.values()) {
            map.put(type.getId(), type);
        }
        TYPE_MAP = Collections.unmodifiableMap(map);
    }

    public static NotificationType getById(int id) {
        return TYPE_MAP.get(id);
    }
}
