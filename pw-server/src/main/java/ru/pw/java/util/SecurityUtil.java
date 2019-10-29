package ru.pw.java.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Lev_S
 */

public abstract class SecurityUtil {

    private static final String IV = "encryptionIntVec";

    private static final String KEY = "r3PFFrAzn4de058v";

    private static final SecretKeySpec SECRET_KEY_SPEC = new SecretKeySpec(KEY.getBytes(), "AES");

    private SecurityUtil() {}

    public static String encode(String wordForEncrypt) {
        try {
            IvParameterSpec initV = new IvParameterSpec(IV.getBytes("UTF-8"));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY_SPEC, initV);

            byte[] encoded = cipher.doFinal(wordForEncrypt.getBytes("UTF-8"));

            return Base64.encodeBase64String(encoded);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decode(String wordForDecrypt) {
        try {
            IvParameterSpec initV = new IvParameterSpec(IV.getBytes("UTF-8"));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY_SPEC, initV);

            byte[] decoded = cipher.doFinal(Base64.decodeBase64(wordForDecrypt));

            return new String(decoded);
        } catch (Exception e){
            e.printStackTrace();
        }

        return  null;
    }

    public static Cookie createCookie(Integer Id) {
        Cookie cookie = new Cookie("userId", SecurityUtil.encode(Id.toString()));

        cookie.setPath("/");
        cookie.setMaxAge(60*60);

        return cookie;
    }

    public static void dropCookie(Cookie[] cookies, HttpServletResponse response) {
        for (Cookie cookie: cookies) {

            cookie.setValue("");
            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);
        }
    }
}

