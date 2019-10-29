package ru.pw.telegram.java.model.enums;

import lombok.Getter;

/**
 * @author Lev_S
 */

public enum PwBotConfig {
    BOT_TOKEN("978542577:AAG-Affno23Xp2CnmT-DdHqOmGQX7VVreGQ"),
    BOT_USERNAME("study_words_bot"),

    LOGTAG_NOTIFICATION("= NOTIFICATION FROM SERVER ="),
    LOGTAG_EXECUTE("= SENDING MESSAGE TO USER ="),
    LOGTAG_RECEIVED_UPDATE("= RECEIVED MESSAGE FROM TG ="),
    LOGTAG_STARTUP("= APPLICATION STARTUP =");

    @Getter
    String value;

    PwBotConfig(String value) {
        this.value = value;
    }
}
