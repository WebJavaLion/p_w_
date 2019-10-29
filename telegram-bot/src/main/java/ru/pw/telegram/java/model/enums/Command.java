package ru.pw.telegram.java.model.enums;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lev_S
 */

public enum Command {
    START_COMMAND("/start"),
    ADD_GROUP_COMMAND("Добавить набор \u2705"),
    SHOW_GROUPS("Мои наборы слов \uD83D\uDCDA"),
    DIALOG_COMMAND("/dialog"),
    ADD_WORD_COMMAND("add_new_word"),
    ACTIONS_COMMAND("button_for_getting_group_actions"),
    DELETE_WORD("delete_word"),
    START_REPETITION_COMMAND("start_repetition"),
    NOTIFICATE_COMMAND("notificate_group"),
    SET_TIME_COMMAND("set_time"),
    VIEW_NOTIFICATIONS("Повторить сегодня \uD83D\uDD01"),
    DELETE_GROUP("delete_group"),
    SHOW_MIXING_MODE("show_repetition_mode"),
    SET_MIXING_MODE("set_mode");

    @Getter
    String value;

    Command(String value) {
        this.value = value;
    }

    public static final Map<String, Command> TYPE_MAP;

    static {
        Map<String, Command> map = new HashMap<>();
        for (Command cmd : Command.values()) {
            map.put(cmd.getValue(), cmd);
        }
        TYPE_MAP = Collections.unmodifiableMap(map);
    }

    public static Command getByMsg(String msg) {

        if (TYPE_MAP.get(msg) != null) {
            return TYPE_MAP.get(msg);
        } else {
            return TYPE_MAP.get(Command.DIALOG_COMMAND.getValue());
        }
    }

    public static Command getByQuery(CallbackQuery cbQuery) {
        String command;
        if (cbQuery.getData().indexOf(' ') != -1) {
            command = cbQuery.getData().substring(0, cbQuery.getData().indexOf(' '));
        } else {
            command = cbQuery.getData();
        }
        return TYPE_MAP.get(command);
    }
}
