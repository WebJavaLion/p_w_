package ru.pw.telegram.java.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.pw.telegram.java.client.ServerPwRestClient;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.interfaces.CommandProcessor;
import ru.pw.telegram.java.model.interfaces.Sender;
import ru.pw.telegram.java.model.param.MessagePojo;
import ru.pw.telegram.java.repository.BotSessionRepository;
import ru.pw.telegram.java.repository.UserRepository;
import ru.pw.telegram.java.repository.WordRepository;
import ru.pw.telegram.java.tables.daos.GroupRepeatNotificationDao;
import ru.pw.telegram.java.tables.daos.WordBotSessionDao;
import ru.pw.telegram.java.tables.daos.WordDao;
import ru.pw.telegram.java.tables.daos.WordGroupDao;
import ru.pw.telegram.java.tables.pojos.GroupRepeatNotification;
import ru.pw.telegram.java.tables.pojos.WordGroup;

import java.sql.Date;
import java.util.Optional;

/**
 * @author Lev_S
 */

@Component
public class SetTimeProcessor implements CommandProcessor {

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

    @Autowired
    GroupRepeatNotificationDao notificationDao;

    private final static int ONE_HOUR_IN_MILS = 86400000;
    @Override
    public Command getCommand() {
        return Command.SET_TIME_COMMAND;
    }

    @Override
    public void execute(Sender sender, CallbackQuery query) {
        String[] buttonDefinition = query.getData().split(" ");

        int days = Integer.parseInt(buttonDefinition[2]);
        GroupRepeatNotification notification = new GroupRepeatNotification();

        Optional<WordGroup> wordGroupOptional = wordRepository
                .getWordGroupByGroupId(Integer.parseInt(buttonDefinition[1]));
        if (wordGroupOptional.isPresent()) {

            notification.setGroupId(Integer.parseInt(buttonDefinition[1]));
            notification.setIsSent(false);
            notification.setNotificationDate(new Date(new java.util.Date().getTime() + (ONE_HOUR_IN_MILS * days)));

            if ("every".equals(buttonDefinition[buttonDefinition.length - 1])) {
                notification.setIsRepeated(true);
                notification.setDaysForRepeat(days);
            } else {
                notification.setIsRepeated(false);
                notification.setDaysForRepeat(null);
            }
            notificationDao.insert(notification);

            sender.sendMessage(new MessagePojo(
                    query.getFrom().getId().longValue(),
                    "Дни для повторения добавлены!",
                    query.getId())
            );
        } else {
            sender.sendMessage(new MessagePojo(null, null, query.getId()));
        }
    }
}
