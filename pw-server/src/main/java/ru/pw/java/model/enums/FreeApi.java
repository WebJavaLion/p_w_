package ru.pw.java.model.enums;

import lombok.Getter;

/**
 * @author Lev_S
 */

public enum FreeApi {
    LOGIN("/user/login"),
    REGISTRATION("/user/registration"),
    AUTH_TELEGRAM("/telegram/auth/code"),
    CHECK_AUTH_TELEGRAM("/token/is/actual");

    @Getter
    private String name;

    FreeApi(String name) {
        this.name = name;
    }
}
