package ru.pw.telegram.java.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.interfaces.CommandProcessor;
import ru.pw.telegram.java.model.interfaces.Sender;
import ru.pw.telegram.java.model.param.MessagePojo;
import ru.pw.telegram.java.repository.WordRepository;
import ru.pw.telegram.java.tables.pojos.WordGroup;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * @author Lev_S
 */

@Component
public class ShowGroupsProcessor implements CommandProcessor {

    @Autowired
    WordRepository wordRepository;

    @Override
    public Command getCommand() {
        return Command.SHOW_GROUPS;
    }

    @Override
    public void execute(Sender sender, Message message) {
        List<WordGroup> userGroups = wordRepository.getWordGroupsByTelegramId(message.getFrom().getId());

        if (!userGroups.isEmpty()) {
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
                            "Ваши наборы слов \u2B07\u2B07\u2B07",
                            keyboardMarkup
                    )
            );
        } else {
            sender.sendMessage(new MessagePojo(message.getChatId(), "У вас еще нет групп"));
        }
    }
}
