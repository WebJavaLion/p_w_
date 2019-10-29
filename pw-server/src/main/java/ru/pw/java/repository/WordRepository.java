package ru.pw.java.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.pw.java.Sequences;
import ru.pw.java.model.pojo.WordGroupPojo;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.tables.daos.FileTempDao;
import ru.pw.java.tables.daos.WordDao;
import ru.pw.java.tables.daos.WordGroupDao;
import ru.pw.java.tables.pojos.FileTemp;
import ru.pw.java.tables.pojos.Word;
import ru.pw.java.tables.pojos.WordGroup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ru.pw.java.Tables.FILE_TEMP;
import static ru.pw.java.Tables.WORD;
import static ru.pw.java.Tables.WORD_GROUP;

/**
 * @author Lev_S
 */

@Repository
public class WordRepository {

    @Autowired
    DSLContext dslContext;

    @Autowired
    WordDao wordDao;

    @Autowired
    FileTempDao fileTempDao;

    @Autowired
    WordGroupDao wordGroupDao;

    @Autowired
    PwRequestContext pwRequestContext;

    public Optional<WordGroup> getWordGroupByEntityId(Integer entityId) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(WORD_GROUP)
                        .where(
                                WORD_GROUP.ENTITY_ID.eq(entityId)
                                        .and(WORD_GROUP.DELETED_DATE.isNull())
                        )
                        .fetchOneInto(WordGroup.class)
        );
    }

    public Optional<WordGroup> getWordGroupById(Integer id) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(WORD_GROUP)
                        .where(
                                WORD_GROUP.ID.eq(id)
                                        .and(WORD_GROUP.DELETED_DATE.isNull())
                        )
                        .fetchOneInto(WordGroup.class)
        );
    }

    public List<WordGroup> getWordGroupList() {
        return dslContext
                .selectFrom(WORD_GROUP)
                .where(
                        WORD_GROUP.USER_ID.eq(pwRequestContext.getCurrentUser().getId())
                                .and(WORD_GROUP.DELETED_DATE.isNull())
                )
                .fetchInto(WordGroup.class);
    }

    public Integer createFile(byte[] bytes) {
        Integer nextId = dslContext.nextval(Sequences.FILE_TEMP_ID_SEQ);

        FileTemp fileTemp = new FileTemp();

        fileTemp.setId(nextId);
        fileTemp.setUserFile(bytes);

        fileTempDao.insert(fileTemp);

        return fileTemp.getId();
    }

    public void deleteFileById(Integer fileId) {
        fileTempDao.deleteById(fileId);
    }

    public Integer createWordGroup(WordGroup group) {
        WordGroup wg = fillGroupCreation(group);

        wordGroupDao.insert(wg);

        return wg.getId();
    }

    public Integer updateGroup(WordGroup group) {
        WordGroup wg = fillGroupCreation(group);

        wg.setEntityId(group.getEntityId());

        wordGroupDao.insert(group);

        return wg.getId();
    }

    public void createWordList(List<Word> wordList, Integer groupId) {
        List<Word> list = new ArrayList<>();

        for (Word word : wordList) {
            Word w = new Word();

            w.setId(null);
            w.setCreatedDate(new Timestamp(new Date().getTime()));
            w.setGroupId(groupId);
            w.setWordOriginal(word.getWordOriginal());
            w.setWordTranslation(word.getWordTranslation());

            list.add(word);
        }

        wordDao.insert(list);
    }

    private WordGroup fillGroupCreation(WordGroup group) {
        WordGroup wg = new WordGroup();

        Integer nextId = dslContext.nextval(Sequences.WORD_GROUP_ID_SEQ);

        wg.setId(nextId);
        wg.setCreatedDate(new Timestamp(new Date().getTime()));
        wg.setUserId(pwRequestContext.getCurrentUser().getId());
        wg.setName(group.getName());
        wg.setEntityId(group.getId());
        wg.setMixingModeId(group.getMixingModeId());

        return wg;
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

    public void setDeletedDate(Integer entityId) {
        dslContext.update(WORD_GROUP)
                .set(WORD_GROUP.DELETED_DATE, new Timestamp(new Date().getTime()))
                .where(
                        WORD_GROUP.DELETED_DATE.isNull()
                                .and(WORD_GROUP.ENTITY_ID.eq(entityId))
                )
                .execute();
    }

}
