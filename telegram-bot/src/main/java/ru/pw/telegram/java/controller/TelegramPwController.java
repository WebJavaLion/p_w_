package ru.pw.telegram.java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pw.telegram.java.model.dto.NotificationDto;
import ru.pw.telegram.java.service.TelegramPwService;
import ru.pw.telegram.java.tables.daos.GroupRepeatNotificationDao;

/**
 * @author Lev_S
 */

@RestController
@RequestMapping(path = "/bot")
public class TelegramPwController {

    @Autowired
    GroupRepeatNotificationDao notificationDao;

    @Autowired
    TelegramPwService service;

    @PostMapping(value = "/send/notification")
    public void sendNotification(@RequestBody NotificationDto dto) {
        service.sendTelegramNotification(dto);
    }
}
