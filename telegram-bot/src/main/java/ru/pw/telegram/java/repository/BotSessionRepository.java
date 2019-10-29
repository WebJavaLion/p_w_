package ru.pw.telegram.java.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.pw.telegram.java.model.enums.Status;
import ru.pw.telegram.java.tables.WordGroup;
import ru.pw.telegram.java.tables.daos.WordBotSessionDao;
import ru.pw.telegram.java.tables.daos.WordDao;
import ru.pw.telegram.java.tables.pojos.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.pw.telegram.java.tables.Users.USERS;
import static ru.pw.telegram.java.tables.Word.WORD;
import static ru.pw.telegram.java.tables.WordGroup.WORD_GROUP;
import static ru.pw.telegram.java.tables.WordBotSession.WORD_BOT_SESSION;
import ru.pw.telegram.java.tables.pojos.WordBotSession;

/**
 * @author Lev_S
 */

@Repository
public class BotSessionRepository {

    @Autowired
    WordBotSessionDao dao;

    @Autowired
    DSLContext dslContext;

    public void createNewSession(Integer telegramId) {
        WordBotSession session = new WordBotSession();

        session.setTelegramId(telegramId);
        session.setCurrentWordGroupId(null);
        session.setIsStarted(true);
        session.setStatus(Status.DEFAULT.getValue());
        session.setWordOriginal(null);
        session.setWordTranslated(null);
        session.setShufflingWords(null);

        dao.insert(session);
    }

    public void setSessionInDefaultCondition(WordBotSession session) {
        session.setStatus(Status.DEFAULT.getValue());
        session.setCurrentRepeatedWords(null);
        session.setCurrentRepeatedWordId(null);
        session.setWordOriginal(null);
        session.setWordTranslated(null);
        session.setCurrentRepeatedWordId(null);
        session.setCurrentWordGroupId(null);
        session.setShufflingWords(null);
    }

    public Optional<WordBotSession> getSessionByTelegramId (Integer telegramId) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(WORD_BOT_SESSION)
                        .where(WORD_BOT_SESSION.TELEGRAM_ID.eq(telegramId))
                        .fetchOneInto(WordBotSession.class)
        );
    }
}
