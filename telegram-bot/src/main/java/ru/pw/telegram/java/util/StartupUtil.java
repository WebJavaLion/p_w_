package ru.pw.telegram.java.util;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

/**
 * @author musixin
 */
public abstract class StartupUtil {

    private StartupUtil() {
    }

    public static Properties loadPropertyFile() {
        Properties prop = null;
        InputStream is;

        try {
            prop = new Properties();
            is = ClassLoader
                    .class
                    .getResourceAsStream("/application.properties");
            prop.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return prop;
    }

    public static DefaultBotOptions loadBotOptions(Properties prop) {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        prop.getProperty("proxy.user"),
                        prop.getProperty("proxy.password").toCharArray()
                );
            }
        });

        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
        botOptions.setProxyHost(prop.getProperty("proxy.host"));
        botOptions.setProxyPort(Integer.valueOf(prop.getProperty("proxy.port")));
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
        return botOptions;
    }
}
