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
import ru.pw.telegram.java.tables.daos.WordBotSessionDao;
import ru.pw.telegram.java.tables.daos.WordDao;
import ru.pw.telegram.java.tables.daos.WordGroupDao;
import ru.pw.telegram.java.tables.pojos.WordGroup;

import java.util.Optional;

/**
 * @author Lev_S
 */

@Component
public class SetMixingModeProcessor implements CommandProcessor {
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
        return Command.SET_MIXING_MODE;
    }

    @Override
    public void execute(Sender sender, CallbackQuery query) {

        String[] buttonDefinition = query.getData().split(" ");

        Optional<WordGroup> wordGroupOptional = wordRepository
                .getWordGroupByGroupId(Integer
                        .parseInt(buttonDefinition[buttonDefinition.length - 1]));

        if (wordGroupOptional.isPresent()) {
            WordGroup wordGroup = wordGroupOptional.get();

            wordGroup.setMixingModeId(Integer.parseInt(buttonDefinition[buttonDefinition.length - 2]));
            wordGroupDao.update(wordGroup);

            sender.sendMessage(
                    new MessagePojo(
                            query.getFrom().getId().longValue(),
                            "Режим изменен",
                            query.getId()
                    )
            );
        } else {
            sender.sendMessage(new MessagePojo(null, null, query.getId()));
        }
    }
}
