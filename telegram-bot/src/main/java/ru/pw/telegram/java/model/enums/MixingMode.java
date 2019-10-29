package ru.pw.telegram.java.model.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lev_S
 */

public enum MixingMode {
    RANDOM(1, "Случайно"),
    ORIGINAL_TO_TRANSLATION(2, "Вводить перевод"),
    TRANSLATION_TO_ORIGINAL(3, "Вводить оригинал");

    @Getter
    Integer value;
    @Getter
    String name;

    private final static Map<Integer, MixingMode> MODE_MAP;

    MixingMode(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    static {
        Map<Integer, MixingMode> map = new HashMap<>();

        for (MixingMode mode : MixingMode.values()) {
            map.put(mode.getValue(), mode);
        }
        MODE_MAP = Collections.unmodifiableMap(map);
    }

    public static MixingMode getByValue(Integer value) {
        return MODE_MAP.get(value);
    }
}
