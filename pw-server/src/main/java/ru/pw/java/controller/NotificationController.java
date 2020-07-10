package ru.pw.java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.pw.java.service.NotificationService;

import java.util.List;

/**
 * @author Lev_S
 */

@RestController
@RequestMapping(path = "/notification")
@Transactional
public class NotificationController {

    final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PutMapping(value = "/confirmation/{id}")
    public void confirmationNotification(@PathVariable Integer id) {
        notificationService.markNotificationAsSent(id);
    }

    @PostMapping("/{groupEntityId}/create")
    public void createNotifications(@PathVariable Integer groupEntityId, @RequestBody List<String> dates) {
        notificationService.createNotifications(groupEntityId, dates);
    }
}
