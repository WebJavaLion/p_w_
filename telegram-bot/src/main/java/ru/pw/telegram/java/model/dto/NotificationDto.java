package ru.pw.telegram.java.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lev_S
 */

@Getter
@Setter
@NoArgsConstructor
public class NotificationDto {
    private Long notificationId;
    private Long telegramChatId;
    private String notificationText;
}
