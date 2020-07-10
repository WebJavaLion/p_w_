package ru.pw.java.repository;

import org.jooq.DSLContext;
import org.jooq.InsertValuesStep2;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import ru.pw.java.Keys;
import ru.pw.java.enums.PwRoles;
import ru.pw.java.model.pojo.UsersPojo;
import ru.pw.java.tables.daos.UsersDao;
import ru.pw.java.tables.pojos.Users;
import ru.pw.java.tables.pojos.WordGroup;
import ru.pw.java.tables.records.UsersRecord;
import ru.pw.java.tables.records.UsersRolesRecord;

import java.util.List;
import java.util.Optional;

import static ru.pw.java.Tables.*;

/**
 * @author Lev_S
 */

@Repository
public class UserRepository {

    private final UsersDao usersDao;
    private final DSLContext dslContext;
    private final PasswordEncoder passwordEncoder;

    public UserRepository(DSLContext dslContext,
                          UsersDao usersDao,
                          @Lazy PasswordEncoder passwordEncoder) {

        this.dslContext = dslContext;
        this.usersDao = usersDao;
        this.passwordEncoder = passwordEncoder;
    }

    public UsersPojo userWithRoles(Integer id) {
        final UsersRecord usersRecord = dslContext
                .selectFrom(USERS)
                .where(USERS.ID.eq(id))
                .fetchOne();

        return new UsersPojo(
                usersRecord.into(Users.class),
                usersRecord
                        .fetchChildren(Keys.WORD_GROUP__WORD_GROUP_FK0)
                        .into(WordGroup.class)
        );
    }

    public List<PwRoles> getRolesByUserId(Integer userId) {
        return dslContext
                .select(USERS_ROLES.ROLE)
                .from(USERS_ROLES)
                .where(USERS_ROLES.USER_ID.eq(userId))
                .fetchInto(PwRoles.class);
    }

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

    public void setRolesForUser(Integer id, boolean isAdmin) {
        final InsertValuesStep2<UsersRolesRecord, Integer, PwRoles> query =
                dslContext
                        .insertInto(USERS_ROLES, USERS_ROLES.USER_ID, USERS_ROLES.ROLE)
                        .values(id, PwRoles.USER);
        if (isAdmin) {
            query.values(id, PwRoles.ADMIN);
        }
        query.execute();
    }

    public List<Users> getAllUsers() {
        return dslContext
                .select(USERS.fields())
                .from(USERS)
                .fetchInto(Users.class);
    }

    public Users createUser(String mail, String password) {
        Users user = new Users();

        user.setUserEmail(mail);
        user.setUserPassword(passwordEncoder.encode(password));

        return dslContext
                .insertInto(USERS)
                .set(dslContext.newRecord(USERS, user))
                .returning(USERS.fields())
                .fetchOne()
                .into(Users.class);
    }

    public Optional<Users> getUserByEmail(String email) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(USERS)
                        .where(USERS.USER_EMAIL.eq(email))
                        .fetchOneInto(Users.class)
        );
    }

    public Optional<Users> loadUserByUsername(String username) {
        return dslContext
                .selectFrom(USERS)
                .where(USERS.USER_EMAIL.eq(username))
                .fetchOptionalInto(Users.class);
    }
}
