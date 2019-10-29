package ru.pw.telegram.java.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.enums.Status;
import ru.pw.telegram.java.model.interfaces.CommandProcessor;
import ru.pw.telegram.java.model.interfaces.Sender;
import ru.pw.telegram.java.model.param.MessagePojo;
import ru.pw.telegram.java.repository.BotSessionRepository;
import ru.pw.telegram.java.tables.daos.WordBotSessionDao;
import ru.pw.telegram.java.tables.pojos.WordBotSession;

import java.util.Optional;

/**
 * @author Lev_S
 */

@Component
public class AddGroupsProcessor implements CommandProcessor {

    @Autowired
    BotSessionRepository sessionRepository;

    @Autowired
    WordBotSessionDao sessionDao;

    @Override
    public Command getCommand() {
        return Command.ADD_GROUP_COMMAND;
    }

    @Override
    public void execute(Sender sender, Message message) {
        Optional<WordBotSession> sessionOptional = sessionRepository.getSessionByTelegramId(message.getFrom().getId());

        String textMessage;
        if (sessionOptional.isPresent()) {
            WordBotSession session = sessionOptional.get();
            session.setStatus(Status.CREATING_GROUP.getValue());

            textMessage = "Введите название нового набора";
            sessionDao.update(session);
        } else {
            textMessage = "Введите '/start' ";
        }

        sender.sendMessage(new MessagePojo(message.getFrom().getId().longValue(), textMessage));
    }
}
