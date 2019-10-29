package ru.pw.telegram.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;
import ru.pw.telegram.java.model.enums.PwBotConfig;
import ru.pw.telegram.java.service.TelegramPwService;

/**
 * @author Lev_S
 */

@ServletComponentScan
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            telegramBotsApi.registerBot(context.getBean(TelegramPwService.class));
        } catch (TelegramApiException e) {
            BotLogger.error(PwBotConfig.LOGTAG_STARTUP.getValue(), e);
        }
    }
}
