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
import ru.pw.telegram.java.tables.pojos.Word;
import ru.pw.telegram.java.tables.pojos.WordBotSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Lev_S
 */

@Component
public class DeleteWordProcessor implements CommandProcessor {

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
        return Command.DELETE_WORD;
    }

    @Override
    public void execute(Sender sender, CallbackQuery query) {

        String[] buttonDefinition = query.getData().split(" ");

        Optional<WordBotSession> sessionOptional = sessionRepository.getSessionByTelegramId(query.getFrom().getId());

        if (sessionOptional.isPresent()) {
            WordBotSession session = sessionOptional.get();

            if (wordRepository.getWordGroupByGroupId((Integer.parseInt(buttonDefinition[buttonDefinition.length - 1]))).isPresent()) {
                List<Word> wordList = wordRepository.getWordsByWordGroupId(
                        Integer.parseInt(buttonDefinition[buttonDefinition.length - 1])
                );

                String result = "Введите индекс слова для удаления \n";

                if (!wordList.isEmpty()) {
                    List<String> indexingList = new ArrayList<>();

                    for (Word word : wordList) {
                        String w = wordList.indexOf(word) + ")" + word.getWordOriginal() + " : " + word.getWordTranslation() + "\n";
                        indexingList.add(w);
                    }

                    StringBuilder builder = new StringBuilder(result);
                    for (String s : indexingList) {
                        builder.append(s);
                    }
                    result = builder.toString();
                    session.setStatus(Status.DELETING.getValue());
                    session.setCurrentWordGroupId(Integer.parseInt(buttonDefinition[buttonDefinition.length - 1]));
                } else {
                    sessionRepository.setSessionInDefaultCondition(session);
                    result = "У вас еще нет слов в этой группе";
                }
                sessionDao.update(session);

                sender.sendMessage(new MessagePojo(
                        query.getFrom().getId().longValue(),
                        result,
                        query.getId())
                );
            } else {
                sender.sendMessage(new MessagePojo(null, null, query.getId()));
            }
        }
    }
}
