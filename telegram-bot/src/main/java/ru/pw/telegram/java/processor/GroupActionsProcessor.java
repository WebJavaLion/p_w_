package ru.pw.telegram.java.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
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
import ru.pw.telegram.java.tables.pojos.Word;
import ru.pw.telegram.java.tables.pojos.WordGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author Lev_S
 */

@Component
public class GroupActionsProcessor implements CommandProcessor {

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
        return Command.ACTIONS_COMMAND;
    }

    @Override
    public void execute(Sender sender, CallbackQuery query) {

        String data = query.getData();
        String messageText;

        if (data.contains(" ")) {
            String[] dataDefinition = data.split(" ");

            int wordGroupId = Integer.parseInt(dataDefinition[dataDefinition.length - 1]);

            sessionRepository.getSessionByTelegramId(query.getFrom().getId())
                    .ifPresent(session -> {
                        session.setCurrentWordGroupId(wordGroupId);
                        sessionDao.update(session);
            });

            Optional<WordGroup> wordGroup = wordRepository.getWordGroupByGroupId(wordGroupId);

            if (wordGroup.isPresent()) {
                List<Word> wordList = wordRepository.getWordsByWordGroupId(wordGroupId);

                messageText = getWordsTable(wordList);

                if (messageText != null) {
                    messageText = "<pre>" + "Слова из набора '" + wordGroup.get().getName() + "'"
                            + getWordsTable(wordList)
                            + "</pre>";
                } else {
                    messageText = "Данный набор пока что пуст!";
                }
                ReplyKeyboard keyboardMarkup = getGroupActionsKeyBoard(wordGroupId, wordList);

                sender.sendMessage(new MessagePojo(
                        query.getFrom().getId().longValue(),
                        messageText,
                        keyboardMarkup,
                        query.getId())
                );
            } else {
                sender.sendMessage(new MessagePojo(null, null, query.getId()));
            }
        }
    }
    public ReplyKeyboard getGroupActionsKeyBoard(int groupId, List<Word> wordList) {

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();

        rowInline1.add(
                new InlineKeyboardButton()
                        .setText("Повторить набор")
                        .setCallbackData(Command.START_REPETITION_COMMAND.getValue() + " " + groupId)
        );

        rowInline2.add(
                new InlineKeyboardButton()
                        .setText("Добавить слова")
                        .setCallbackData(Command.ADD_WORD_COMMAND.getValue() + " " + groupId)
        );
        rowInline2.add(
                new InlineKeyboardButton()
                        .setText("Удалить слова")
                        .setCallbackData(Command.DELETE_WORD.getValue() + " " + groupId)
        );

        rowInline3.add(
                new InlineKeyboardButton()
                        .setText("Напоминания")
                        .setCallbackData(Command.NOTIFICATE_COMMAND.getValue() + " " + groupId)
        );
        rowInline3.add(
                new InlineKeyboardButton()
                        .setText("Режимы повторения")
                        .setCallbackData(Command.SHOW_MIXING_MODE.getValue() + " " + groupId)
        );

        rowInline4.add(
                new InlineKeyboardButton()
                        .setText("Удалить группу")
                        .setCallbackData(Command.DELETE_GROUP.getValue() + " " + groupId)
        );

        boolean zeroWords = wordList.size() == 0;

        if (!zeroWords) {
            rowsInline.add(rowInline1);
        }

        rowsInline.add(rowInline2);

        if (!zeroWords) {
            rowsInline.add(rowInline3);
        }

        rowsInline.add(rowInline4);

        keyboardMarkup.setKeyboard(rowsInline);
        return keyboardMarkup;
    }
    public String getWordsTable(List<Word> wordList) {

        String messageText;
        if (!CollectionUtils.isEmpty(wordList)) {
            String maxFirstWord = "||" + wordList.stream().map(Word::getWordOriginal).max(Comparator.comparing(String::length))
                    .orElseThrow(RuntimeException::new) + "|";

            String maxSecondWord = wordList.stream().map(Word::getWordTranslation).max(Comparator.comparing(String::length))
                    .orElseThrow(RuntimeException::new) + "||";

            StringBuilder topSeparatorBuilder = new StringBuilder();
            while (topSeparatorBuilder.length() <= maxFirstWord.length() + maxSecondWord.length()) {
                topSeparatorBuilder.append("=");
            }
            StringBuilder bottomSeparatorBuilder = new StringBuilder();
            while (bottomSeparatorBuilder.length() <= maxFirstWord.length() + maxSecondWord.length()) {
                bottomSeparatorBuilder.append("-");
            }
            StringBuilder resultBuilder = new StringBuilder();

            wordList.forEach(word -> {
                String wordOriginal = word.getWordOriginal();
                String wordTranslation = word.getWordTranslation();
                wordOriginal = "||" + wordOriginal;

                StringBuilder originalBuilder = new StringBuilder(wordOriginal);
                StringBuilder translationBuilder = new StringBuilder(wordTranslation);

                while (maxFirstWord.length() - originalBuilder.length() >= 1) {
                    originalBuilder.append(" ");
                }
                wordOriginal = originalBuilder.toString() + "|";

                while (maxSecondWord.length() - translationBuilder.length() >= 1) {

                    if (maxSecondWord.length() - translationBuilder.length() == 1) {
                        translationBuilder.append("||");
                    } else {
                        translationBuilder.append(" ");
                    }
                }
                wordTranslation = translationBuilder.toString();
                resultBuilder.append(wordOriginal).append(wordTranslation).append("\n");
            });

            messageText = "\n" + topSeparatorBuilder.toString()+"=" + "\n"
                    + resultBuilder.toString() + "\r"
                    + bottomSeparatorBuilder.toString()+ "-";
        } else {
            messageText = null;
        }
        return messageText;
    }
}