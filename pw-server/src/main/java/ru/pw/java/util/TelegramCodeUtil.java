package ru.pw.java.util;

import ru.pw.java.tables.pojos.Users;

import java.util.Date;

/**
 * @author Lev_S
 */

public abstract class TelegramCodeUtil {

    private TelegramCodeUtil() {
    }

    public static String encodeKeyForLinkWEBandBOT(Users user) {
        return SecurityUtil.encode("user=" + user.getId());
    }

    public static String encodeKeyForAuthorization() {
        return SecurityUtil.encode("auth=" + new Date().getTime());
    }
}
