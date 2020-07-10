package ru.pw.java.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

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


            IvParameterSpec initV = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY_SPEC, initV);

            byte[] encoded = cipher.doFinal(wordForEncrypt.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeBase64String(encoded);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decode(String wordForDecrypt) {
        try {
            IvParameterSpec initV = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));

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

