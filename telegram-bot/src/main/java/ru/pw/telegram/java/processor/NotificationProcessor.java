package ru.pw.telegram.java.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pw.telegram.java.client.ServerPwRestClient;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.interfaces.CommandProcessor;
import ru.pw.telegram.java.model.interfaces.Sender;
import ru.pw.telegram.java.model.param.MessagePojo;
import ru.pw.telegram.java.repository.BotSessionRepository;
import ru.pw.telegram.java.repository.UserRepository;
import ru.pw.telegram.java.repository.WordRepository;
import ru.pw.telegram.java.tables.daos.WordBotSessionDao;
import ru.pw.telegram.java.tables.daos.WordDao;
import ru.pw.telegram.java.tables.daos.WordGroupDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lev_S
 */

@Component
public class NotificationProcessor implements CommandProcessor {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ServerPwRestClient client;

    @Autowired
    WordDao wordDao;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    BotSessionRepository sessionRepository;

    @Autowired
    WordBotSessionDao sessionDao;

    @Autowired
    WordGroupDao wordGroupDao;

    @Override
    public Command getCommand() {
        return Command.NOTIFICATE_COMMAND;
    }


    @Override
    public void execute(Sender sender, CallbackQuery query) {
        String[] buttonDefinition = query.getData().split(" ");

        int groupId = Integer.parseInt(buttonDefinition[buttonDefinition.length - 1]);

        if (wordRepository.getWordGroupByGroupId(groupId).isPresent()) {
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

            List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
            List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
            List<InlineKeyboardButton> rowInline3 = new ArrayList<>();

            rowInline1.add(
                    new InlineKeyboardButton()
                            .setText("Через 1 день")
                            .setCallbackData(Command.SET_TIME_COMMAND.getValue() + " " + groupId + " 1 day")
            );
            rowInline1.add(
                    new InlineKeyboardButton()
                            .setText("Каждый день")
                            .setCallbackData(Command.SET_TIME_COMMAND.getValue() + " " + groupId + " 1 day every")
            );

            rowInline2.add(
                    new InlineKeyboardButton()
                            .setText("Через 3 день")
                            .setCallbackData(Command.SET_TIME_COMMAND.getValue() + " " + groupId + " 3 days")
            );
            rowInline2.add(
                    new InlineKeyboardButton()
                            .setText("Каждые 3 дня")
                            .setCallbackData(Command.SET_TIME_COMMAND.getValue() + " " + groupId + " 3 days every")
            );

            rowInline3.add(
                    new InlineKeyboardButton()
                            .setText("Через 5 день")
                            .setCallbackData(Command.SET_TIME_COMMAND.getValue() + " " + groupId + " 5 days")
            );
            rowInline3.add(
                    new InlineKeyboardButton()
                            .setText("Каждые 5 дней")
                            .setCallbackData(Command.SET_TIME_COMMAND.getValue() + " " + groupId + " 5 days every")
            );

            rowsInline.add(rowInline1);
            rowsInline.add(rowInline2);
            rowsInline.add(rowInline3);

            keyboardMarkup.setKeyboard(rowsInline);

            sender.sendMessage(new MessagePojo(
                    query.getFrom().getId().longValue(),
                    "Выберите дни для повторений",
                    keyboardMarkup,
                    query.getId())
            );
        } else {
            sender.sendMessage(new MessagePojo(null, null, query.getId()));
        }
    }
}
