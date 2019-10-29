package ru.pw.telegram.java.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import ru.pw.telegram.java.client.ServerPwRestClient;
import ru.pw.telegram.java.model.dto.CurrentRepeatedDto;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.enums.MixingMode;
import ru.pw.telegram.java.model.enums.Status;
import ru.pw.telegram.java.model.interfaces.CommandProcessor;
import ru.pw.telegram.java.model.interfaces.Sender;
import ru.pw.telegram.java.model.param.MessagePojo;
import ru.pw.telegram.java.model.param.RepeatedWordsPojo;
import ru.pw.telegram.java.repository.BotSessionRepository;
import ru.pw.telegram.java.repository.UserRepository;
import ru.pw.telegram.java.repository.WordRepository;
import ru.pw.telegram.java.tables.daos.WordBotSessionDao;
import ru.pw.telegram.java.tables.daos.WordDao;
import ru.pw.telegram.java.tables.daos.WordGroupDao;
import ru.pw.telegram.java.tables.pojos.Word;
import ru.pw.telegram.java.tables.pojos.WordBotSession;
import ru.pw.telegram.java.tables.pojos.WordGroup;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Lev_S
 */

@Component
public class DialogProcessor implements CommandProcessor {

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
    GroupActionsProcessor groupProcessor;

    @Autowired
    WordGroupDao wordGroupDao;

    @Override
    public Command getCommand() {
        return Command.DIALOG_COMMAND;
    }

    @Override
    public void execute(Sender sender, Message message) {
        Optional<WordBotSession> sessionOptional = sessionRepository.getSessionByTelegramId(message.getFrom().getId());

        String textMessage = "";
        ReplyKeyboard keyboard = null;
        if (sessionOptional.isPresent()) {
            WordBotSession session = sessionOptional.get();

            Status sessionStatus = Status.getStatus(session.getStatus());
            switch (sessionStatus) {

                case DEFAULT:
                    textMessage = "Введите /start для начала работы";
                    break;

                case START_TRANSLATING:
                    String[] wordsFromMessage = message.getText().split(",");
                    List<Word> wList = new ArrayList<>();

                    if (wordsFromMessage.length != 1) {

                        if (Arrays.stream(wordsFromMessage).map(s -> s.split(" "))
                                .noneMatch(
                                        collection -> Arrays.stream(collection)
                                                .filter(a -> !"".equals(a)).count() != 2)) {

                            for (String w : wordsFromMessage) {
                                String[] word = w.split(" ");

                                List<String> res = Arrays.stream(word)
                                        .filter(a -> !"".equals(a))
                                        .collect(Collectors.toList());

                                Word newWord = wordRepository.createNewWord(res);
                                newWord.setGroupId(session.getCurrentWordGroupId());
                                wList.add(newWord);
                            }
                            wordDao.insert(wList);

                            textMessage = "<pre> Слова успешно добавлены!"
                                    + groupProcessor.getWordsTable(
                                    wordRepository.getWordsByWordGroupId(session.getCurrentWordGroupId())
                            )
                                    + "</pre>";
                            keyboard = groupProcessor.getGroupActionsKeyBoard(session.getCurrentWordGroupId(), wList);

                        } else {
                            textMessage = "Введите корректный вариант";
                        }
                    } else {
                        String[] eachWord = message.getText().split(" ");
                        List<String> res = Arrays.stream(eachWord)
                                .filter(a -> !"".equals(a))
                                .collect(Collectors.toList());

                        if (eachWord.length == 2) {
                            Word newWord = wordRepository.createNewWord(res);
                            newWord.setGroupId(session.getCurrentWordGroupId());
                            wList.add(newWord);

                            wordDao.insert(wList);
                            textMessage = "<pre> Слово успешно добавлено!"
                                    + groupProcessor.getWordsTable(
                                    wordRepository.getWordsByWordGroupId(session.getCurrentWordGroupId())
                            )
                                    + "</pre>";
                            keyboard = groupProcessor.getGroupActionsKeyBoard(session.getCurrentWordGroupId(), wList);
                        } else {
                            textMessage = "Введите корректный вариант";
                        }
                    }
                    session.setStatus(Status.DEFAULT.getValue());
                    break;

                case CREATING_GROUP:
                    userRepository
                            .getUserByTelegramId(message.getFrom().getId().longValue())
                            .ifPresent(user -> {

                                WordGroup wordGroup = new WordGroup();
                                wordGroup.setCreatedDate(new Timestamp(new Date().getTime()));
                                wordGroup.setName(message.getText());
                                wordGroup.setMixingModeId(MixingMode.RANDOM.getValue());
                                wordGroup.setUserId(user.getId());

                                wordGroupDao.insert(wordRepository.fillGroupCreation(wordGroup));
                            });
                    session.setStatus(Status.DEFAULT.getValue());
                    textMessage = "Набор успешно добавлен!";
                    break;

                case REHEARSING:

                    List<Word> wordList = null;
                    try {
                        wordList = new ObjectMapper().readValue(
                                new StringReader(session.getShufflingWords()), new TypeReference<List<Word>>() {
                                });
                    } catch (IOException e) {
                        sessionRepository.setSessionInDefaultCondition(session);
                        sessionDao.update(session);
                        e.printStackTrace();
                    }
                    if (!CollectionUtils.isEmpty(wordList)) {

                        StringReader stringReader = null;
                        RepeatedWordsPojo wordsPojo = new RepeatedWordsPojo();
                        StringWriter stringWriter = new StringWriter();

                        Integer mixingModeId = wordGroupDao.fetchOneById(session.getCurrentWordGroupId()).getMixingModeId();
                        MixingMode mixingMode = MixingMode.getByValue(mixingModeId);
                        for (Word w : wordList) {

                            String repeatedWords = session.getCurrentRepeatedWords();

                            ObjectMapper objectMapper = new ObjectMapper();
                            if (repeatedWords != null) {
                                stringReader = new StringReader(repeatedWords);
                            }

                            if (w.getId().equals(session.getCurrentRepeatedWordId())
                                    && !wordList.get(wordList.size() - 1).getId().equals(session.getCurrentRepeatedWordId())) {

                                String originWord = session.getWordTranslated();
                                String resultWord;

                                switch (mixingMode) {

                                    case RANDOM:
                                        if (originWord.equals(w.getWordOriginal())) {

                                            wordsPojo.setWordOriginal(originWord);
                                            wordsPojo.setWordTranslated(message.getText());
                                        }

                                        if (originWord.equals(w.getWordTranslation())) {

                                            wordsPojo.setWordOriginal(message.getText());
                                            wordsPojo.setWordTranslated(originWord);
                                        }
                                        double value = Math.random();

                                        if (value > 0.5) {
                                            textMessage = wordList.get(wordList.indexOf(w) + 1).getWordOriginal();
                                        } else {
                                            textMessage = wordList.get(wordList.indexOf(w) + 1).getWordTranslation();
                                        }
                                        break;

                                    case ORIGINAL_TO_TRANSLATION:
                                        wordsPojo.setWordOriginal(originWord);
                                        wordsPojo.setWordTranslated(message.getText());

                                        textMessage = wordList.get(wordList.indexOf(w) + 1).getWordOriginal();

                                        break;

                                    case TRANSLATION_TO_ORIGINAL:
                                        wordsPojo.setWordOriginal(message.getText());
                                        wordsPojo.setWordTranslated(originWord);

                                        textMessage = wordList.get(wordList.indexOf(w) + 1).getWordTranslation();

                                        break;
                                }
                                session.setCurrentRepeatedWordId(wordList.get(wordList.indexOf(w) + 1).getId());
                                resultWord = textMessage;
                                session.setWordTranslated(resultWord);
                                textMessage = "Переведите слово '" + textMessage + "'";
                                try {
                                    if (repeatedWords == null) {
                                        CurrentRepeatedDto dto = new CurrentRepeatedDto();
                                        List<RepeatedWordsPojo> pojoList = new ArrayList<>();
                                        pojoList.add(wordsPojo);
                                        dto.setRepeatedWordsList(pojoList);

                                        objectMapper.writeValue(stringWriter, dto);
                                    } else {
                                        CurrentRepeatedDto repeatedDto = objectMapper.readValue(stringReader, CurrentRepeatedDto.class);
                                        repeatedDto.getRepeatedWordsList().add(wordsPojo);

                                        objectMapper.writeValue(stringWriter, repeatedDto);
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException("couldnt write value");
                                }
                                session.setCurrentRepeatedWords(stringWriter.toString());

                                break;

                            } else {

                                if (wordList.get(wordList.size() - 1).getId().equals(session.getCurrentRepeatedWordId())) {
                                    int counter = 0;
                                    CurrentRepeatedDto repeatedDto;

                                    if (wordList.size() != 1) {
                                        try {
                                            repeatedDto = objectMapper.readValue(stringReader, CurrentRepeatedDto.class);
                                        } catch (Exception e) {
                                            throw new RuntimeException("couldnt read value");
                                        }
                                    } else {
                                        repeatedDto = new CurrentRepeatedDto();
                                        repeatedDto.setRepeatedWordsList(new ArrayList<>());
                                    }
                                    RepeatedWordsPojo pojo = new RepeatedWordsPojo();

                                    List<RepeatedWordsPojo> repeatedList = repeatedDto.getRepeatedWordsList();
                                    if (wordList.get(wordList.size() - 1).getWordTranslation().equals(session.getWordTranslated())) {
                                        pojo.setWordTranslated(session.getWordTranslated());
                                        pojo.setWordOriginal(message.getText());
                                    }

                                    if (wordList.get(wordList.size() - 1).getWordOriginal().equals(session.getWordTranslated())) {
                                        pojo.setWordTranslated(message.getText());
                                        pojo.setWordOriginal(session.getWordTranslated());
                                    }
                                    repeatedList.add(pojo);
                                    StringBuilder builder = new StringBuilder(textMessage);

                                    for (Word original : wordList) {

                                        if (wordList.indexOf(original) + 1 <= wordList.size()) {

                                            if (!original.getWordOriginal().trim().equalsIgnoreCase(repeatedList.get(wordList.indexOf(original)).getWordOriginal())) {
                                                counter++;
                                                builder.append(original.getWordTranslation())
                                                        .append(" : ")
                                                        .append(repeatedList.get(wordList.indexOf(original)).getWordOriginal())
                                                        .append(" ❌ right:")
                                                        .append(original.getWordOriginal())
                                                        .append("\n");
                                            }

                                            if (!original.getWordTranslation().trim().equalsIgnoreCase(repeatedList.get(wordList.indexOf(original)).getWordTranslated())) {
                                                counter++;
                                                builder.append(original.getWordOriginal())
                                                        .append(" : ")
                                                        .append(repeatedList.get(wordList.indexOf(original)).getWordTranslated())
                                                        .append(" ❌ right:")
                                                        .append(original.getWordTranslation())
                                                        .append("\n");
                                            }

                                            if (original.getWordOriginal().trim().equalsIgnoreCase(repeatedList.get(wordList.indexOf(original)).getWordOriginal())
                                                    && original.getWordTranslation().equalsIgnoreCase(
                                                    repeatedList.get(wordList.indexOf(original)).getWordTranslated())) {

                                                builder.append(original.getWordOriginal())
                                                        .append(" : ")
                                                        .append(repeatedList.get(wordList.indexOf(original)).getWordTranslated())
                                                        .append(" ✅")
                                                        .append("\n");
                                            }
                                        }
                                    }
                                    textMessage = "<pre>" + builder.toString() + "количество ошибок: " + counter + "</pre>";
                                    sessionRepository.setSessionInDefaultCondition(session);
                                }
                            }
                        }
                    }
                    break;

                case DELETING:
                    try {
                        int wordIndex = Integer.parseInt(message.getText());
                        List<Word> words = wordRepository.getWordsByWordGroupId(
                                session.getCurrentWordGroupId());

                        wordDao.delete(words.get(wordIndex));

                        Word deletedWord = words.remove(wordIndex);
                        textMessage = "<pre>"
                                + "Пара слов  '" + deletedWord.getWordOriginal()
                                + "' : '" + deletedWord.getWordTranslation() + "' была удалена!"
                                + groupProcessor.getWordsTable(words)
                                + "</pre>";

                        keyboard = groupProcessor.getGroupActionsKeyBoard(session.getCurrentWordGroupId(), words);

                        sessionRepository.setSessionInDefaultCondition(session);
                    } catch (IllegalArgumentException e) {
                        textMessage = "Выберите правильный индекс!";
                    }
                    break;
            }
            sessionDao.update(session);

        } else {
            textMessage = "Введите /start для начала работы";
        }
        sender.sendMessage(new MessagePojo(message.getChatId(), textMessage, keyboard));
    }
}