package ru.pw.telegram.java.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ru.pw.telegram.java.model.enums.NotificationType;
import ru.pw.telegram.java.tables.daos.UsersDao;
import ru.pw.telegram.java.tables.pojos.Users;
import ru.pw.telegram.java.tables.pojos.Word;

import java.util.List;
import java.util.Optional;

import static ru.pw.telegram.java.tables.UserAuthorizationToken.USER_AUTHORIZATION_TOKEN;
import static ru.pw.telegram.java.tables.Users.USERS;


/**
 * @author Lev_S
 */

@Repository
public class UserRepository {

    @Autowired
    UsersDao dao;

    @Autowired
    DSLContext dslContext;

    public void updateTelegramAttributes(Long userId, Long tgUserId, Long tgChatId) {
        Users users = dao.fetchOneById(userId.intValue());

        users.setTelegramUserId(tgUserId.intValue());
        users.setTelegramChatId(tgChatId);

        dao.update(users);
    }

    public void createNewUser(Long tgUserId, Long tgChatId) {
        Users users = new Users();

        users.setTelegramUserId(tgUserId.intValue());
        users.setTelegramChatId(tgChatId);
        users.setNotificationType(NotificationType.TELEGRAM.getId());

        dao.insert(users);
    }

    public void updateChatId(Users user, Long tgChatId) {
        Users users = new Users(user);
        users.setTelegramChatId(tgChatId);
        dao.update();
    }

    public Optional<Users> getUserByTelegramId(Long tgUserId) {
        return Optional.ofNullable(dao.fetchOne(USERS.TELEGRAM_USER_ID, tgUserId.intValue()));
    }

    public void markTokenAsNotActual(String token) {
        dslContext
                .update(USER_AUTHORIZATION_TOKEN)
                .set(
                        USER_AUTHORIZATION_TOKEN.IS_ACTUAL,
                        false
                )
                .where(USER_AUTHORIZATION_TOKEN.TOKEN.eq(token))
                .execute();
    }

}
