package ru.pw.java.repository;

import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import ru.pw.java.Keys;
import ru.pw.java.Sequences;
import ru.pw.java.model.pojo.WordGroupPojo;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.tables.daos.FileTempDao;
import ru.pw.java.tables.daos.WordDao;
import ru.pw.java.tables.daos.WordGroupDao;
import ru.pw.java.tables.pojos.FileTemp;
import ru.pw.java.tables.pojos.Word;
import ru.pw.java.tables.pojos.WordGroup;
import ru.pw.java.tables.records.WordGroupRecord;
import ru.pw.java.tables.records.WordRecord;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ru.pw.java.Tables.*;

/**
 * @author Lev_S
 */

@Repository
public class WordRepository {

    final WordDao wordDao;
    final DSLContext dslContext;
    final FileTempDao fileTempDao;
    final WordGroupDao wordGroupDao;
    final PwRequestContext pwRequestContext;

    public WordRepository(WordDao wordDao,
                          DSLContext dslContext,
                          FileTempDao fileTempDao,
                          WordGroupDao wordGroupDao,
                          PwRequestContext pwRequestContext) {

        this.wordDao = wordDao;
        this.dslContext = dslContext;
        this.fileTempDao = fileTempDao;
        this.wordGroupDao = wordGroupDao;
        this.pwRequestContext = pwRequestContext;
    }

    public Optional<WordGroup> getActualWordGroupByEntityId(Integer entityId) {
        return dslContext
                .selectFrom(WORD_GROUP)
                .where(
                        WORD_GROUP.ENTITY_ID.eq(entityId)
                                .and(WORD_GROUP.DELETED_DATE.isNull())
                )
                .fetchOptionalInto(WordGroup.class);
    }

    public Optional<WordGroup> getWordGroupById(Integer id) {
        return dslContext
                .selectFrom(WORD_GROUP)
                .where(
                        WORD_GROUP.ID.eq(id)
                                .and(WORD_GROUP.DELETED_DATE.isNull())
                )
                .fetchOptionalInto(WordGroup.class)
                .map(wg -> new WordGroupPojo(wg, getWordsByGroupId(wg.getEntityId())));
    }

    public Set<WordGroup> getSetU() {
        return dslContext
                .select(WORD_GROUP.fields())
                .from(WORD_GROUP)
                .fetchSet(w -> w.into(WordGroup.class));
    }

    public List<WordGroupPojo> getWordGroupsForUser(Integer userId) {
        final Result<WordGroupRecord> wordGroupRecordResult = dslContext.selectFrom(WORD_GROUP)
                .where(WORD_GROUP.USER_ID.eq(userId)
                        .and(WORD_GROUP.DELETED_DATE.isNull())
                )
                .fetch();

        final Result<WordRecord> wordRecordResult = dslContext
                .selectFrom(WORD)
                .where(WORD.GROUP_ID.in(
                        wordGroupRecordResult.stream()
                                .map(WordGroupRecord::getId)
                                .collect(Collectors.toList())
                        )
                )
                .fetch();

        return wordGroupRecordResult.stream()
                .map(rec -> new WordGroupPojo(
                                rec.into(WordGroup.class),
                                wordRecordResult.stream()
                                        .filter(wordRec -> wordRec.getGroupId().equals(rec.getId()))
                                        .map(wordRec -> wordRec.into(Word.class))
                                        .collect(Collectors.toList())
                        )
                )
                .collect(Collectors.toList());
    }

    public Integer createFile(byte[] bytes) {
        return dslContext
                .insertInto(FILE_TEMP)
                .set(FILE_TEMP.USER_FILE, bytes)
                .returning(FILE_TEMP.ID)
                .execute();

    }

    public Word createWord(Word word) {
        word.setCreatedDate(Timestamp.from(Instant.now().truncatedTo(ChronoUnit.SECONDS)).toLocalDateTime());
        return dslContext
                .insertInto(WORD)
                .set(dslContext.newRecord(WORD, word))
                .returning()
                .fetchOne()
                .into(Word.class);
    }

    public Map<Integer, List<WordGroup>> get() {
        return dslContext
                .selectFrom(WORD_GROUP)
                .fetchGroups(WORD_GROUP.USER_ID, WordGroup.class);
    }


    public Map<LocalDateTime, ? extends List<WordGroup>> count(Timestamp timestamp) {

        return dslContext.select(
                WORD_GROUP.fields())
                .from(WORD_GROUP)
                .where(
                        DSL.trunc(
                                WORD_GROUP.DELETED_DATE, DatePart.DAY)
                                .le(timestamp.toLocalDateTime()))
                .fetchGroups(WORD_GROUP.DELETED_DATE, WordGroup.class);
    }


    public void deleteFileById(Integer fileId) {
        fileTempDao.deleteById(fileId);
    }

    public Integer createWordGroup(WordGroup group) {
        fillGroupCreation(group);
        wordGroupDao.insert(group);

        return group.getId();
    }

    public Integer updateGroup(WordGroup group) {
        fillGroupCreation(group);
        wordGroupDao.insert(group);

        return group.getId();
    }

    public void createWordList(List<Word> wordList, Integer groupId) {
        wordList.forEach(w -> {
            w.setId(null);
            w.setCreatedDate(new Timestamp(new Date().getTime()).toLocalDateTime());
            w.setGroupId(groupId);
        });
        wordDao.insert(wordList);
    }

    public void createWs(List<Word> words) {
        dslContext
                .batchInsert(words.stream()
                        .map(word -> dslContext.newRecord(WORD, word))
                        .collect(Collectors.toList())
                )
                .execute();
    }

    private void fillGroupCreation(WordGroup group) {
        Integer nextId = dslContext.nextval(Sequences.WORD_GROUP_ID_SEQ);

        group.setId(nextId);
        group.setCreatedDate(Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES)).toLocalDateTime());
        group.setDeletedDate(null);
    }

    public Optional<FileTemp> getFileById(Integer id) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(FILE_TEMP)
                        .where(FILE_TEMP.ID.eq(id))
                        .fetchOneInto(FileTemp.class)
        );
    }

    public List<Word> getWordsByGroupId(Integer groupId) {
        return dslContext
                .selectFrom(WORD)
                .where(WORD.GROUP_ID.eq(groupId))
                .fetchInto(Word.class);
    }

    public void setDeletedDateByEntityId(Integer entityId) {
        dslContext
                .update(WORD_GROUP)
                .set(WORD_GROUP.DELETED_DATE, Timestamp.from(Instant.now()).toLocalDateTime())
                .where(
                        WORD_GROUP.DELETED_DATE.isNull()
                                .and(WORD_GROUP.ENTITY_ID.eq(entityId))
                )
                .execute();
    }

    public Integer deleteWordById(Integer id) {
        return dslContext
                .deleteFrom(WORD)
                .where(WORD.ID.eq(id))
                .execute();
    }

    public Optional<Integer> getUserIdByWordId(Integer wordId) {
        return dslContext
                .selectFrom(WORD)
                .where(WORD.ID.eq(wordId))
                .fetchOptional()
                .map(wordRecord -> wordRecord.fetchParent(Keys.WORD__WORD_FK0)
                        .getUserId()
                );
    }

    public Optional<Word> updateWord(Word word) {
        return dslContext.update(WORD)
                .set(dslContext.newRecord(WORD, word))
                .where(WORD.ID.eq(word.getId()))
                .returning()
                .fetchOptional()
                .map(r -> r.into(Word.class));
    }
}
