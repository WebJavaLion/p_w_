package ru.pw.java.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.pw.java.model.enums.ValidationError;
import ru.pw.java.model.exception.ValidateException;
import ru.pw.java.model.pojo.WordGroupPojo;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.repository.WordRepository;
import ru.pw.java.tables.pojos.FileTemp;
import ru.pw.java.tables.pojos.Word;
import ru.pw.java.tables.pojos.WordGroup;
import ru.pw.java.util.ParserUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Lev_S
 */

@Service
public class WordService {

    private static final Logger log = LogManager.getLogger(WordService.class);

    @Autowired
    WordRepository repository;

    @Autowired
    PwRequestContext pwRequestContext;

    public Optional<WordGroupPojo> getGroupByEntityId(Integer entityId) {
        Optional<WordGroup> group = repository.getWordGroupByEntityId(entityId);

        if (group.isPresent() && pwRequestContext.getCurrentUser().getId().equals(group.get().getUserId())) {
            WordGroupPojo groupPojo = new WordGroupPojo(group.get());

            groupPojo.setWordList(repository.getWordsByGroupId(groupPojo.getId()));

            return Optional.of(groupPojo);
        } else {
            return Optional.empty();
        }
    }

    public List<WordGroup> getCurrentUserGroups() {
        return repository.getWordGroupList();
    }

    public Integer uploadFileWithWords(MultipartFile file) throws IOException {
        return repository.createFile(file.getBytes());
    }

    public void updateWordGroup(WordGroupPojo pojo) throws IOException {
        validate(pojo);

        if (pojo.getEntityId() != null) {
            Optional<WordGroup> optionalWordGroup = repository.getWordGroupByEntityId(pojo.getEntityId());

            if (optionalWordGroup.isPresent()) {
                WordGroup wg = optionalWordGroup.get();

                if (pwRequestContext.getCurrentUser().getId().equals(wg.getUserId())) {
                    repository.setDeletedDate(wg.getEntityId());

                    Integer id = repository.updateGroup(pojo);
                    fillWordsFromFile(pojo);

                    repository.createWordList(pojo.getWordList(), id);
                } else {
                    throw new ValidateException(ValidationError.INVALID_WORD_GROUP_VERIFICATION.getText());
                }
            } else {
                throw new ValidateException(ValidationError.INVALID_WORD_GROUP.getText());
            }
        } else {
            throw new ValidateException(ValidationError.INVALID_WORD_GROUP.getText());
        }
    }

    private void fillWordsFromFile(WordGroupPojo pojo) throws IOException {
        if (pojo.getTempFileId() != null) {
            Optional<FileTemp> fileOpt = repository
                    .getFileById(pojo.getTempFileId().intValue());

            if (fileOpt.isPresent()) {
                FileTemp file = fileOpt.get();

                List<Word> words = ParserUtil.parseExcel(file.getUserFile());
                pojo.setWordList(words);

                repository.deleteFileById(file.getId());
            }
        }
    }

    public void createWordGroup(WordGroupPojo pojo) throws IOException {
        validate(pojo);

        Integer groupId = repository.createWordGroup(pojo);
        fillWordsFromFile(pojo);

        repository.createWordList(pojo.getWordList(), groupId);
    }

    private void validate(WordGroupPojo pojo) {
        List<String> errorList = new ArrayList<>();

        if (StringUtils.isEmpty(pojo.getName())) {
            errorList.add(ValidationError.INVALID_NAME.getText());
        }

        if (ObjectUtils.isEmpty(pojo.getMixingModeId())) {
            errorList.add(ValidationError.INVALID_MIXING_MODE.getText());
        }

        if (ObjectUtils.isEmpty(pojo.getTempFileId()) && CollectionUtils.isEmpty(pojo.getWordList())) {
            errorList.add(ValidationError.INVALID_CONTENT.getText());
        }

        if (!CollectionUtils.isEmpty(pojo.getWordList())) {
            for (Word word : pojo.getWordList()) {
                if (StringUtils.isEmpty(word.getWordOriginal()) || StringUtils.isEmpty(word.getWordTranslation())) {
                    errorList.add(ValidationError.INVALID_WORD.getText());
                    break;
                }
            }
        }

        if (!errorList.isEmpty()) {
            log.error("Failed try to create word group");

            throw new ValidateException(errorList);
        }
    }
}

