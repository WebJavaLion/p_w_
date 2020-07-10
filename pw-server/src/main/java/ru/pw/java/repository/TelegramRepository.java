package ru.pw.java.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.pw.java.tables.daos.UserAuthorizationTokenDao;
import ru.pw.java.tables.pojos.UserAuthorizationToken;

import static ru.pw.java.Tables.USER_AUTHORIZATION_TOKEN;

/**
 * @author Lev_S
 */

@Repository
public class TelegramRepository {

    private final DSLContext dslContext;
    private final UserAuthorizationTokenDao userAuthorizationTokenDao;

    public TelegramRepository(DSLContext dslContext,
                              UserAuthorizationTokenDao userAuthorizationTokenDao) {
        this.dslContext = dslContext;
        this.userAuthorizationTokenDao = userAuthorizationTokenDao;
    }

    public void createToken(String hash) {
        UserAuthorizationToken token = new UserAuthorizationToken();
        token.setToken(hash);
        token.setIsActual(true);
        userAuthorizationTokenDao.insert(token);
    }

    public boolean tokenIsActual(String token) {
        return dslContext
                .select(USER_AUTHORIZATION_TOKEN.IS_ACTUAL)
                .from(USER_AUTHORIZATION_TOKEN)
                .where(USER_AUTHORIZATION_TOKEN.TOKEN.eq(token))
                .fetchOne(0, Boolean.class);
    }
}
