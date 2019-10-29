package ru.pw.telegram.java.model.interfaces;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.pw.telegram.java.model.enums.Command;
import ru.pw.telegram.java.model.interfaces.Sender;

import java.io.IOException;

/**
 * @author Lev_S
 */

public interface CommandProcessor  {

    Command getCommand();

    default void execute(Sender sender, Message ob) {};

    default void execute(Sender sender, CallbackQuery query) {};

}
