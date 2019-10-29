package ru.pw.telegram.java.util;

import org.apache.commons.codec.binary.Base64;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author musixin
 */
public abstract class TelegramCodeUtil {

    private static final String IV = "encryptionIntVec";

    private static final String KEY = "r3PFFrAzn4de058v";

    private static final SecretKeySpec SECRET_KEY_SPEC = new SecretKeySpec(KEY.getBytes(), "AES");

    private TelegramCodeUtil() {
    }

    public static String decodeKey(String startHash) {
        try {
            IvParameterSpec initV = new IvParameterSpec(IV.getBytes("UTF-8"));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY_SPEC, initV);

            byte[] decoded = cipher.doFinal(Base64.decodeBase64(startHash));

            return new String(decoded);
        } catch (Exception e){}
        return "";
    }
}
