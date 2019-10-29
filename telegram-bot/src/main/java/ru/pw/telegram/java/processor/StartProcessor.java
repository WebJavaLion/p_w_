package ru.pw.telegram.java.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.interfaces.CommandProcessor;
import ru.pw.telegram.java.model.interfaces.Sender;
import ru.pw.telegram.java.model.param.MessagePojo;
import ru.pw.telegram.java.repository.BotSessionRepository;
import ru.pw.telegram.java.repository.UserRepository;
import ru.pw.telegram.java.tables.pojos.Users;
import ru.pw.telegram.java.util.TelegramCodeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Lev_S
 */

@Component
public class StartProcessor implements CommandProcessor {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BotSessionRepository sessionRepository;

    @Override
    public Command getCommand() {
        return Command.START_COMMAND;
    }

    /**
     * Все, что начинается со /start
     * @param sender
     * @param message
     */
    @Override
    public void execute(Sender sender, Message message) {
        String text = message.getText();

        boolean startWithParam = text.indexOf(' ') != -1
                && text.substring(text.indexOf(' ')).length() > 0;

        if (startWithParam) {
            String token = text.substring(text.indexOf(' '));
            String decodedStr = TelegramCodeUtil.decodeKey(token);

            if (!isEmpty(decodedStr) && decodedStr.contains("=")) {
                String[] parts = decodedStr.split("=");

                String mode = parts[0];

                Long telegramUserId = message.getFrom().getId().longValue();
                Long telegramChatId = message.getChatId();

                String greeting;
                if ("auth".equals(mode)) {
                    Optional<Users> user = userRepository.getUserByTelegramId(telegramUserId);

                    if (user.isPresent()) {
                        userRepository.updateChatId(user.get(), telegramChatId);

                        greeting = String
                                .format(
                                        "Добро пожаловать %s, вы успешно авторизованы",
                                        message.getFrom().getFirstName()
                                );
                    } else {
                        userRepository.createNewUser(telegramUserId, telegramChatId);

                        greeting = String
                                .format(
                                        "Добро пожаловать %s, вы успешно зарегистрированы",
                                        message.getFrom().getFirstName()
                                );
                    }
                } else {
                    Long applicationUserId = Long.valueOf(parts[1]);

                    userRepository.updateTelegramAttributes(
                            applicationUserId,
                            telegramUserId,
                            telegramChatId
                    );

                    greeting = String
                            .format(
                                    "Добро пожаловать %s, вы успешно авторизовались",
                                    message.getFrom().getFirstName()
                            );
                }

                if (!sessionRepository.getSessionByTelegramId(message.getChatId().intValue()).isPresent()) {
                    sessionRepository.createNewSession(message.getChatId().intValue());
                }
                userRepository.markTokenAsNotActual(token);

                sender.sendMessage(new MessagePojo(message.getChatId(), greeting, getCommonKeyBoard()));
            } else {
                sender.sendMessage(new MessagePojo(message.getChatId(), "Некорректный параметр"));
            }
        } else {
            if (!sessionRepository.getSessionByTelegramId(message.getChatId().intValue()).isPresent()) {
                userRepository.createNewUser(message.getFrom().getId().longValue(), message.getChatId());
                sessionRepository.createNewSession(message.getChatId().intValue());
            }

            sender.sendMessage(
                    new MessagePojo(message.getChatId(),
                            "Привет, это бот для изучения и повторения слов!", getCommonKeyBoard())
            );
        }
    }

    private ReplyKeyboard getCommonKeyBoard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> rowLine = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();

        row1.add(new KeyboardButton().setText(Command.ADD_GROUP_COMMAND.getValue()));
        row2.add(new KeyboardButton().setText(Command.SHOW_GROUPS.getValue()));
        row3.add(new KeyboardButton().setText(Command.VIEW_NOTIFICATIONS.getValue()));

        rowLine.add(0, row1);
        rowLine.add(1, row2);
        rowLine.add(2, row3);

        keyboardMarkup.setKeyboard(rowLine);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);

        return keyboardMarkup;
    }
}
