package ru.pw.java.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pw.java.repository.NotificationRepository;
import ru.pw.java.service.NotificationService;
import ru.pw.java.tables.pojos.GroupRepeatNotification;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Lev_S
 */

@Component
public class NotificationScheduler {

    final NotificationService service;

    final NotificationRepository repository;

    private final long ONE_HOUR = 60 * 60 * 1000;

    public NotificationScheduler(NotificationService service, NotificationRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Scheduled(fixedDelay = ONE_HOUR)
    public void sendNotifications() {
        List<GroupRepeatNotification> notifications =
                repository.getActualNotificationsByDate(LocalDate.now());

        notifications
                .parallelStream()
                .forEach(service::sendNotification);
    }
}
