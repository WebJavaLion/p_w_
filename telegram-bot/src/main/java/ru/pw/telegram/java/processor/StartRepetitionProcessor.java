package ru.pw.telegram.java.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.pw.telegram.java.client.ServerPwRestClient;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.enums.MixingMode;
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
import ru.pw.telegram.java.tables.pojos.WordGroup;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Lev_S
 */

@Component
public class StartRepetitionProcessor implements CommandProcessor {
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
        return Command.START_REPETITION_COMMAND;
    }

    @Override
    public void execute(Sender sender, CallbackQuery query) {

        Optional<WordBotSession> sessionOptional = sessionRepository.getSessionByTelegramId(query.getFrom().getId());

        String[] data = query.getData().split(" ");

        String resultWord = "";

        if (sessionOptional.isPresent()) {
            WordBotSession session = sessionOptional.get();

            sessionRepository.setSessionInDefaultCondition(session);

            List<Word> wordList = wordRepository.getWordsByWordGroupId(
                    Integer.parseInt(data[data.length - 1]));
            Collections.shuffle(wordList);

            ObjectMapper objectMapper = new ObjectMapper();

            try {
                String wordListAsJson = objectMapper.writeValueAsString(wordList);
                session.setShufflingWords(wordListAsJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            Optional<WordGroup> wordGroupOptional = wordRepository.getWordGroupByGroupId(Integer.parseInt(data[data.length - 1]));

            if (wordGroupOptional.isPresent()) {
                WordGroup wg = wordGroupOptional.get();

                if (!wordList.isEmpty()) {
                    Word word = wordList.get(0);

                    session.setStatus(Status.REHEARSING.getValue());

                    MixingMode mixingMode = MixingMode.getByValue(wg.getMixingModeId());
                    switch (mixingMode) {

                        case RANDOM:
                            double value = Math.random();

                            if (value > 0.5) {
                                resultWord = word.getWordOriginal();
                            } else {
                                resultWord = word.getWordTranslation();
                            }
                            break;

                        case ORIGINAL_TO_TRANSLATION:
                            resultWord = word.getWordOriginal();
                            break;

                        case TRANSLATION_TO_ORIGINAL:
                            resultWord = word.getWordTranslation();
                            break;
                    }
                    session.setWordTranslated(resultWord);
                    session.setCurrentRepeatedWordId(word.getId());
                    session.setCurrentWordGroupId(Integer.parseInt(data[data.length - 1]));

                    sessionDao.update(session);

                    sender.sendMessage(new MessagePojo(
                            query.getFrom().getId().longValue(),
                            "Переведите слово '" + resultWord + "'",
                            query.getId())
                    );
                }
            } else {
                sender.sendMessage(new MessagePojo( null, null, query.getId()));
            }
        }
    }
}
