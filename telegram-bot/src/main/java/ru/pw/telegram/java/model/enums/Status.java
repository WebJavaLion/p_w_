package ru.pw.telegram.java.model.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lev_S
 */

public enum Status {
    DEFAULT(0),
    START_TRANSLATING(1),
    FINISHING_TRANSLATION(2),
    CREATING_GROUP(3),
    REHEARSING(4),
    DELETING(5);

    public static final Map<Integer, Status> STATUS_MAP;

    @Getter
    Integer value;

    Status(Integer value) {
        this.value = value;
    }

    static {
        Map<Integer, Status> map = new HashMap<>();
        for (Status status : Status.values()) {
            map.put(status.getValue(), status);
        }
        STATUS_MAP = Collections.unmodifiableMap(map);
    }

    public static Status getStatus(Integer value) {

        return STATUS_MAP.get(value);
    }
}
