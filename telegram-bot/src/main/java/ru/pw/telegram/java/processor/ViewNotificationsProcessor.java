package ru.pw.telegram.java.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
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
import ru.pw.telegram.java.tables.pojos.GroupRepeatNotification;
import ru.pw.telegram.java.tables.pojos.WordGroup;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * @author Lev_S
 */

@Component
public class ViewNotificationsProcessor implements CommandProcessor {

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
        return Command.VIEW_NOTIFICATIONS;
    }

    @Override
    public void execute(Sender sender, Message message) {
        List<WordGroup> userGroups = wordRepository.getWordGroupsByTelegramIdForToday(message.getFrom().getId());

        if (!CollectionUtils.isEmpty(userGroups)) {
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

            Queue<WordGroup> queue = new ArrayDeque<>(userGroups);

            int countOfRows = (int) Math.ceil((double)userGroups.size() / 2);
            for (int row = 0; row < countOfRows; row++) {
                List<InlineKeyboardButton> rowInline = new ArrayList<>();

                for (int column = 0; column < 2; column++) {
                    WordGroup wg = queue.poll();
                    if (wg != null) {
                        rowInline.add(
                                new InlineKeyboardButton()
                                        .setText(wg.getName())
                                        .setCallbackData(
                                                Command.ACTIONS_COMMAND.getValue()
                                                        + " " + wg.getName()
                                                        + " " + wg.getId()
                                        )
                        );
                    }
                }

                rowsInline.add(rowInline);
            }

            keyboardMarkup.setKeyboard(rowsInline);

            sender.sendMessage(
                    new MessagePojo(
                            message.getChatId(),
                            "Вам нужно повторить эти наборы \u2B07\u2B07\u2B07",
                            keyboardMarkup
                    )
            );
        } else {
            sender.sendMessage(
                    new MessagePojo(
                            message.getChatId(),
                            "У вас нет наборов для повторения"
                    )
            );
        }
    }
}
