package ru.pw.java.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.pw.java.Sequences;
import ru.pw.java.tables.daos.UserAuthorizationTokenDao;
import ru.pw.java.tables.daos.UsersDao;
import ru.pw.java.tables.pojos.UserAuthorizationToken;
import ru.pw.java.tables.pojos.Users;
import ru.pw.java.util.SecurityUtil;

import java.util.List;
import java.util.Optional;

import static ru.pw.java.Tables.USERS;
import static ru.pw.java.Tables.USER_AUTHORIZATION_TOKEN;
import static ru.pw.java.Tables.WORD_GROUP;

/**
 * @author Lev_S
 */

@Repository
public class UserRepository {

    @Autowired
    DSLContext dslContext;

    @Autowired
    UsersDao usersDao;

    public Optional<Users> getUserByGroup(Integer groupId) {
        return Optional.ofNullable(
                dslContext
                        .select(USERS.fields())
                        .from(USERS)
                        .join(WORD_GROUP).on(USERS.ID.eq(WORD_GROUP.USER_ID))
                        .where(WORD_GROUP.ENTITY_ID.eq(groupId).and(
                                    WORD_GROUP.DELETED_DATE.isNull())
                        )
                        .fetchOneInto(Users.class)
        );
    }

    public Optional<Users> getUserById(Integer id) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(USERS)
                        .where(USERS.ID.eq(id))
                        .fetchOneInto(Users.class)
        );
    }

    public List<Users> getAllUsers() {
        return dslContext.select(USERS.fields()).from(USERS).fetchInto(Users.class);
    }

    public Users createUser(String mail, String password) {
        Integer nextId = dslContext.nextval(Sequences.USERS_ID_SEQ);

        Users user = new Users();

        user.setId(nextId);
        user.setUserEmail(mail);
        user.setUserPassword(SecurityUtil.encode(password));

        usersDao.insert(user);

        return user;
    }

    public Optional<Users> getUserByEmail(String email) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(USERS)
                        .where(USERS.USER_EMAIL.eq(email))
                        .fetchOneInto(Users.class)
        );
    }

    public Optional<Users> getUserByEmailAndPassword(String email, String password) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(USERS)
                        .where(
                                USERS.USER_EMAIL.eq(email)
                                        .and(USERS.USER_PASSWORD.eq(SecurityUtil.encode(password)))
                        )
                        .fetchOneInto(Users.class)
        );
    }
}
