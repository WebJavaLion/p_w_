package ru.pw.telegram.java.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.pw.telegram.java.Sequences;
import ru.pw.telegram.java.tables.daos.WordDao;
import ru.pw.telegram.java.tables.pojos.GroupRepeatNotification;
import ru.pw.telegram.java.tables.pojos.Word;
import ru.pw.telegram.java.tables.pojos.WordGroup;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ru.pw.telegram.java.tables.GroupRepeatNotification.GROUP_REPEAT_NOTIFICATION;
import static ru.pw.telegram.java.tables.Users.USERS;
import static ru.pw.telegram.java.tables.Word.WORD;
import static ru.pw.telegram.java.tables.WordGroup.WORD_GROUP;

/**
 * @author Lev_S
 */

@Repository
public class WordRepository {

    @Autowired
    WordDao dao;

    @Autowired
    DSLContext dslContext;

    public Word createNewWord(List<String> words) {
        Word newWord = new Word();

        newWord.setWordOriginal(words.get(0));
        newWord.setWordTranslation(words.get(1));
        newWord.setCreatedDate(new Timestamp(new Date().getTime()));

        return newWord;
    }

    public Optional<WordGroup> getWordGroupByGroupId(int id) {
        return Optional.ofNullable(
                dslContext
                        .select(WORD_GROUP.fields())
                        .from(WORD_GROUP)
                        .where(WORD_GROUP.ID
                                .eq(id)
                                .and(WORD_GROUP.DELETED_DATE.isNull()))
                        .fetchOneInto(WordGroup.class)
        );
    }

    public List<WordGroup> getWordGroupsByTelegramId(Integer telegramId) {
        return dslContext.select(WORD_GROUP.fields())
                .from(WORD_GROUP)
                .where(WORD_GROUP.DELETED_DATE.isNull().and(
                        WORD_GROUP.USER_ID.in(
                                dslContext.select(USERS.ID)
                                        .from(USERS)
                                        .where(
                                        USERS.TELEGRAM_USER_ID.eq(telegramId)
                                        )
                                    )
                                )
                )
                .fetchInto(WordGroup.class);
    }

    public List<Word> getWordsByWordGroupId(Integer groupId) {
         return dslContext.select(WORD.fields())
                  .from(WORD)
                  .where(WORD.GROUP_ID.eq(groupId))
                  .fetchInto(Word.class);
    }

    public WordGroup fillGroupCreation(WordGroup group) {
        WordGroup wg = new WordGroup();

        Integer nextId = dslContext.nextval(Sequences.WORD_GROUP_ID_SEQ);

        wg.setId(nextId);
        wg.setCreatedDate(new Timestamp(new Date().getTime()));
        wg.setUserId(group.getUserId());
        wg.setName(group.getName());
        wg.setEntityId(wg.getId());
        wg.setMixingModeId(group.getMixingModeId());

        return wg;
    }

    public List<WordGroup> getWordGroupsByTelegramIdForToday(Integer telegramId) {
        Integer serviceUserId = dslContext
                .select(USERS.ID)
                .from(USERS)
                .where(USERS.TELEGRAM_USER_ID.eq(telegramId))
                .fetchOne(0, Integer.class);

        return dslContext
                .selectDistinct(WORD_GROUP.fields())
                .from(WORD_GROUP)
                .join(GROUP_REPEAT_NOTIFICATION).on(GROUP_REPEAT_NOTIFICATION.GROUP_ID.eq(WORD_GROUP.ID))
                .where(
                        WORD_GROUP.USER_ID.eq(serviceUserId).and(
                                GROUP_REPEAT_NOTIFICATION.NOTIFICATION_DATE.eq(java.sql.Date.valueOf(LocalDate.now()))
                        )
                )
                .fetchInto(WordGroup.class);
    }
    public void deleteNotificationsByGroupId(int groupId) {
        dslContext.delete(GROUP_REPEAT_NOTIFICATION)
                .where(GROUP_REPEAT_NOTIFICATION.GROUP_ID.eq(groupId))
                .execute();
    }
}
