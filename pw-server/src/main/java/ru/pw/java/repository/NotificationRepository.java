package ru.pw.java.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.pw.java.tables.pojos.GroupRepeatNotification;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static ru.pw.java.tables.GroupRepeatNotification.GROUP_REPEAT_NOTIFICATION;

/**
 * @author Lev_S
 */

@Repository
public class NotificationRepository {

    final DSLContext dslContext;

    @Autowired
    public NotificationRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public List<GroupRepeatNotification> getActualNotificationsByDate(LocalDate date) {
        return dslContext
                .selectFrom(GROUP_REPEAT_NOTIFICATION)
                .where(
                        GROUP_REPEAT_NOTIFICATION
                                .NOTIFICATION_DATE.eq(Date.valueOf(date).toLocalDate())
                        .and(GROUP_REPEAT_NOTIFICATION.IS_SENT.isFalse())
                )
                .fetchInto(GroupRepeatNotification.class);
    }
}
