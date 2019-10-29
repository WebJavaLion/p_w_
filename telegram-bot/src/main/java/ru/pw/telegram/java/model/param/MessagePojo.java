package ru.pw.telegram.java.model.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

/**
 * @author Lev_S
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessagePojo {

    Long chatId;

    String text;

    ReplyKeyboard keyboardMarkup;

    String callBackId;

    public MessagePojo(Long chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }
    public MessagePojo(Long chatId, String text, String callBackId) {
        this.chatId = chatId;
        this.text = text;
        this.callBackId = callBackId;
    }
    public MessagePojo(Long chatId, String text, ReplyKeyboard keyboardMarkup) {
        this.chatId = chatId;
        this.text = text;
        this.keyboardMarkup = keyboardMarkup;
    }
}
