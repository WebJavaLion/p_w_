package ru.pw.telegram.java.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.pw.telegram.java.client.ServerPwRestClient;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.enums.Status;
import ru.pw.telegram.java.model.interfaces.CommandProcessor;
import ru.pw.telegram.java.model.interfaces.Sender;
import ru.pw.telegram.java.model.param.MessagePojo;
import ru.pw.telegram.java.repository.BotSessionRepository;
import ru.pw.telegram.java.repository.UserRepository;
import ru.pw.telegram.java.repository.WordRepository;
import ru.pw.telegram.java.tables.daos.WordBotSessionDao;
import ru.pw.telegram.java.tables.daos.WordDao;
import ru.pw.telegram.java.tables.daos.WordGroupDao;
import ru.pw.telegram.java.tables.pojos.WordBotSession;

import java.util.Optional;

/**
 * @author Lev_S
 */

@Component
public class AddWordsProcessor implements CommandProcessor {

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
        return Command.ADD_WORD_COMMAND;
    }

    @Override
    public void execute(Sender sender, CallbackQuery query) {

        Optional<WordBotSession> sessionOptional = sessionRepository.getSessionByTelegramId(query.getFrom().getId());

        String[] buttonDefinitions = query.getData().split(" ");
        String textMessage;

        if (sessionOptional.isPresent()) {
            WordBotSession session = sessionOptional.get();

            if (wordRepository.getWordGroupByGroupId(Integer.parseInt(buttonDefinitions[buttonDefinitions.length - 1])).isPresent()) {

                if (Status.DEFAULT == Status.getStatus(session.getStatus())) {
                    session.setStatus(Status.START_TRANSLATING.getValue());
                    session.setCurrentWordGroupId(Integer.parseInt(buttonDefinitions[buttonDefinitions.length - 1]));
                    sessionDao.update(session);

                    textMessage = "Введите слова в формате: оригинал перевод, оригинал перевод ...";
                    sender.sendMessage(new MessagePojo(query.getMessage().getChatId(), textMessage, query.getId()));
                } else {
                    session.setStatus(Status.DEFAULT.getValue());
                    sessionDao.update(session);
                }
            } else {
                sender.sendMessage(new MessagePojo(null, null, query.getId()));
            }
        }
    }
}
