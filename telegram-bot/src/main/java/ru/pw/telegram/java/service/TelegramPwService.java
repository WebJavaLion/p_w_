package ru.pw.telegram.java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;
import ru.pw.telegram.java.client.ServerPwRestClient;
import ru.pw.telegram.java.model.dto.NotificationDto;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.enums.PwBotConfig;
import ru.pw.telegram.java.model.interfaces.CommandProcessor;
import ru.pw.telegram.java.model.param.MessagePojo;
import ru.pw.telegram.java.util.StartupUtil;

import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Lev_S
 */

@Service
public class TelegramPwService extends TelegramLongPollingBot {

    @Autowired
    ServerPwRestClient client;

    private static DefaultBotOptions botOptions;

    private final Map<Command, CommandProcessor> processors =
            new EnumMap<>(Command.class);

    static {
        Properties properties = StartupUtil.loadPropertyFile();
        botOptions = StartupUtil.loadBotOptions(properties);
    }

    public TelegramPwService(@Autowired Set<CommandProcessor> commandProcessors) {
        super(botOptions);

        for (CommandProcessor processor : commandProcessors) {
            processors.put(processor.getCommand(), processor);
        }
    }

    @Override
    public String getBotToken() {
        return PwBotConfig.BOT_TOKEN.getValue();
    }

    @Override
    public String getBotUsername() {
        return PwBotConfig.BOT_USERNAME.getValue();
    }

    @Override
    @Transactional
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();

                if (message.hasText()) {
                    process(message);
                }
            }

            if (update.hasCallbackQuery()) {
                process(update.getCallbackQuery());
            }
        } catch (Exception e) {
            BotLogger.error(PwBotConfig.LOGTAG_RECEIVED_UPDATE.getValue(), e);
        }
    }

    private void process(Message messageItem) {
        Command command = Command.getByMsg(messageItem.getText());

        if (command != null) {
            CommandProcessor commandProcessor = processors.get(command);
            commandProcessor.execute(
                    this::sendMessage,
                    messageItem
            );
        }
    }

    private void process(CallbackQuery query) {
        Command command = Command.getByQuery(query);

        if (command != null) {
            CommandProcessor callbackProcessor = processors.get(command);
            callbackProcessor.execute(
                    this::sendMessage,
                    query
            );
        }
    }

    private void sendMessage(MessagePojo message) {
        SendMessage sm = new SendMessage();
        sm.setParseMode("html");

        if (!ObjectUtils.isEmpty(message.getKeyboardMarkup())) {
            sm.setReplyMarkup(message.getKeyboardMarkup());
        }
        try {
            if (!StringUtils.isEmpty(message.getCallBackId())) {
                execute(new AnswerCallbackQuery().setCallbackQueryId(message.getCallBackId()));
            }
            if (!StringUtils.isEmpty(message.getText()) && !ObjectUtils.isEmpty(message.getChatId())) {
                sm.setChatId(message.getChatId());
                sm.setText(message.getText());
                execute(sm);
            }
        } catch (TelegramApiException e) {
            BotLogger.error(PwBotConfig.LOGTAG_EXECUTE.getValue(), e);
        }
    }

    public void sendTelegramNotification(NotificationDto notificationDto) {
        sendMessage(
                new MessagePojo(
                        notificationDto.getTelegramChatId(),
                        notificationDto.getNotificationText()
                )
        );
        client.sendConfirmationForServer(notificationDto.getNotificationId());
    }
}
