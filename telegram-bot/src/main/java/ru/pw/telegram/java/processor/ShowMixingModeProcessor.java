package ru.pw.telegram.java.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pw.telegram.java.client.ServerPwRestClient;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.enums.MixingMode;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Lev_S
 */

@Component
public class ShowMixingModeProcessor implements CommandProcessor {

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
        return Command.SHOW_MIXING_MODE;
    }

    @Override
    public void execute(Sender sender, CallbackQuery query) {

        String[] buttonValue = query.getData().split(" ");
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        Optional<WordGroup> wordGroupOptional = wordRepository
                .getWordGroupByGroupId(
                        Integer.parseInt(buttonValue[buttonValue.length - 1])
                );

        if (wordGroupOptional.isPresent()) {
            WordGroup wordGroup = wordGroupOptional.get();
            List<List<InlineKeyboardButton>> rowsList = new ArrayList<>();

            List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
            List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

            rowInline1.add(
                    new InlineKeyboardButton()
                            .setText(MixingMode.RANDOM.getName())
                            .setCallbackData(
                                    "set_mode" + " " +
                                            MixingMode.RANDOM.getValue() + " " +
                                            buttonValue[buttonValue.length - 1]
                            )
            );

            rowInline2.add(
                    new InlineKeyboardButton()
                            .setText(MixingMode.ORIGINAL_TO_TRANSLATION.getName())
                            .setCallbackData(
                                    "set_mode" + " " +
                                            MixingMode.ORIGINAL_TO_TRANSLATION.getValue() + " " +
                                            buttonValue[buttonValue.length - 1]
                            )
            );
            rowInline2.add(
                    new InlineKeyboardButton()
                            .setText(MixingMode.TRANSLATION_TO_ORIGINAL.getName())
                            .setCallbackData("set_mode" + " " +
                                    MixingMode.TRANSLATION_TO_ORIGINAL.getValue() + " " +
                                    buttonValue[buttonValue.length - 1]
                            )
            );

            rowsList.add(rowInline1);
            rowsList.add(rowInline2);

            keyboardMarkup.setKeyboard(rowsList);

            sender.sendMessage(
                    new MessagePojo(
                            query.getFrom().getId().longValue(),
                            "Выберите режим повторения.\nТекущий режим - \""
                                    + MixingMode.getByValue(wordGroup.getMixingModeId()).getName()
                                    + "\"",
                            keyboardMarkup,
                            query.getId()
                    )
            );
        } else {
            sender.sendMessage(new MessagePojo(null, null, query.getId()));
        }
    }
}
