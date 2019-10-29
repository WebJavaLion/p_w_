package ru.pw.java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.pw.java.client.TelegramPwBotRestClient;
import ru.pw.java.model.dto.NotificationDto;
import ru.pw.java.model.enums.NotificationMessage;
import ru.pw.java.model.enums.NotificationType;
import ru.pw.java.model.enums.ValidationError;
import ru.pw.java.model.exception.ValidateException;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.repository.UserRepository;
import ru.pw.java.repository.WordRepository;
import ru.pw.java.tables.daos.GroupRepeatNotificationDao;
import ru.pw.java.tables.pojos.GroupRepeatNotification;
import ru.pw.java.tables.pojos.Users;
import ru.pw.java.tables.pojos.WordGroup;
import ru.pw.java.util.SecurityUtil;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Lev_S
 */

@Service
public class NotificationService {

    @Autowired
    PwRequestContext pwRequestContext;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    GroupRepeatNotificationDao dao;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    MailService mailService;

    @Autowired
    TelegramPwBotRestClient client;

    public void sendNotification(GroupRepeatNotification notification) {
        Optional<WordGroup> wordGroupOpt = wordRepository.getWordGroupById(notification.getGroupId());

        if (wordGroupOpt.isPresent()) {
            Optional<Users> userOpt = userRepository.getUserByGroup(notification.getGroupId());

            if (userOpt.isPresent()) {
                Users user = userOpt.get();
                WordGroup wordGroup = wordGroupOpt.get();

                switch (NotificationType.getById(user.getNotificationType())) {
                    case MAIL:
                        mailService
                                .sendNotificationMessage(
                                        user,
                                        wordGroup
                                );
                        markNotificationAsSent(notification.getId());
                        break;
                    case WEB:
                        String webSocketSession = SecurityUtil.encode(user.getId().toString());
                        messagingTemplate.convertAndSendToUser(
                                webSocketSession,
                                "/topic/notsForUser",
                                notification
                        );
                        break;
                    case TELEGRAM:
                        if (user.getTelegramUserId() != null && user.getTelegramChatId() != null) {
                            NotificationDto notificationDto = new NotificationDto();

                            notificationDto
                                    .setNotificationId(notification.getId().longValue());
                            notificationDto
                                    .setTelegramChatId(user.getTelegramChatId());
                            notificationDto
                                    .setNotificationText(
                                            String.format(
                                                    NotificationMessage.TELEGRAM.getMessage(),
                                                    wordGroup.getName()
                                            )
                                    );

                            client.sendNotificationForChat(notificationDto);
                        }
                        break;
                }
            } else {
                throw new ValidateException(ValidationError.INVALID_USER.getText());
            }
        } else {
            throw new ValidateException(ValidationError.INVALID_WORD_GROUP.getText());
        }
    }

    public void createNotifications(Integer entityId, List<String> dates) {
        Optional<WordGroup> wordGroupOptional = wordRepository.getWordGroupByEntityId(entityId);

        if (wordGroupOptional.isPresent()) {
            WordGroup wg = wordGroupOptional.get();

            if (pwRequestContext.getCurrentUser().getId().equals(wg.getUserId())) {
                for (String date : dates) {
                    GroupRepeatNotification notification = new GroupRepeatNotification();

                    notification.setNotificationDate(Date.valueOf(date));
                    notification.setGroupId(wg.getEntityId());
                    notification.setIsSent(false);

                    dao.insert(notification);
                }
            } else {
                throw new ValidateException(ValidationError.INVALID_WORD_GROUP_VERIFICATION.getText());
            }
        } else {
            throw new ValidateException(ValidationError.INVALID_WORD_GROUP.getText());
        }
    }

    public void markNotificationAsSent(Integer id) {
        GroupRepeatNotification notification = dao.findById(id);

        if (!notification.getIsRepeated()) {
            notification.setIsSent(true);
        } else {
            notification.setNotificationDate
                    (new Date
                            (new java.util.Date().getTime() +
                                    (86400000 * notification.getDaysForRepeat()
                                    )
                            ));

        }
        dao.update(notification);
    }
}
