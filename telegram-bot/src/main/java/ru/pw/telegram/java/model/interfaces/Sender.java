package ru.pw.telegram.java.model.interfaces;

import ru.pw.telegram.java.model.param.MessagePojo;

/**
 * @author Lev_S
 */

public interface Sender {

    void sendMessage(MessagePojo message);

}
