package ru.pw.java.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.pw.java.model.enums.ValidationError;
import ru.pw.java.model.exception.ValidateException;
import ru.pw.java.model.pojo.PwUserDetails;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Lev_S
 */

@Service
public class WordService {

    private static final Logger log = LogManager.getLogger(WordService.class);

    private final WordRepository repository;
    private final PwRequestContext pwRequestContext;

    public WordService(WordRepository repository,
                       PwRequestContext pwRequestContext) {
        this.repository = repository;
        this.pwRequestContext = pwRequestContext;
    }

    public Optional<WordGroupPojo> getGroupByEntityId(Integer entityId) {
        Optional<WordGroup> group = repository.getActualWordGroupByEntityId(entityId);

        if (group.isPresent() &&
                pwRequestContext.getCurrentUser()
                        .orElseThrow(RuntimeException::new)
                        .getId()
                        .equals(group.get().getUserId())) {

            WordGroupPojo groupPojo = new WordGroupPojo(group.get());
            groupPojo.setWordList(repository.getWordsByGroupId(groupPojo.getEntityId()));

            return Optional.of(groupPojo);
        } else {
            return Optional.empty();
        }
    }

    public List<WordGroupPojo> getCurrentUserGroups(Integer userId) {
        return repository.getWordGroupsForUser(userId);
    }

    public Integer uploadFileWithWords(MultipartFile file) throws IOException {
        return repository.createFile(file.getBytes());
    }

    public void updateWordGroup(WordGroupPojo pojo) {
        validate(pojo);

        if (pojo.getEntityId() != null) {
            WordGroup wg = repository.getActualWordGroupByEntityId(pojo.getEntityId())
                    .orElseThrow(() -> new ValidateException(ValidationError.INVALID_WORD_GROUP.getText()));

            if (pwRequestContext.getCurrentUser().orElseThrow(RuntimeException::new).getId().equals(wg.getUserId())) {
                repository.setDeletedDateByEntityId(wg.getEntityId());

                Integer id = repository.updateGroup(pojo);
                fillWordsFromFile(pojo);

                repository.createWordList(pojo.getWordList(), id);
            } else {
                throw new ValidateException(ValidationError.INVALID_WORD_GROUP_VERIFICATION.getText());
            }
        } else {
            throw new ValidateException(ValidationError.INVALID_WORD_GROUP.getText());
        }
    }

    private void fillWordsFromFile(WordGroupPojo pojo) {
        if (pojo.getTempFileId() != null) {
            Optional<FileTemp> fileOpt = repository
                    .getFileById(pojo.getTempFileId().intValue());

            if (fileOpt.isPresent()) {
                FileTemp file = fileOpt.get();

                List<Word> words;
                try {
                    words = ParserUtil.parseExcel(file.getUserFile());
                } catch (IOException e) {
                    throw new RuntimeException("fail parse");
                }
                pojo.setWordList(words);

                repository.deleteFileById(file.getId());
            }
        }
    }

    public void createWordGroup(WordGroupPojo pojo) {
        validate(pojo);

        pojo.setUserId(pwRequestContext.getCurrentUser().orElseThrow(RuntimeException::new).getId());
        pojo.setMixingModeId(1);
        Integer groupId = repository.createWordGroup(pojo);

        fillWordsFromFile(pojo);

        repository.createWordList(pojo.getWordList(), groupId);
    }

    private void validate(WordGroupPojo pojo) {

        List<String> errorList = Stream
                .of(
                        validateName(pojo.getName()),
                        validateWords(pojo.getWordList())
                )
                .flatMap(List::stream)
                .collect(Collectors.toList());

        if (!errorList.isEmpty()) {
            log.error("Failed try to create word group");

            throw new ValidateException(errorList);
        }
    }

    private List<String> validateName(String pojoName) {
        List<String> errorList = new ArrayList<>();

        if (StringUtils.isEmpty(pojoName)) {
            errorList.add(ValidationError.INVALID_NAME.getText());
        }
        return errorList;
    }

    private List<String> validateWords(List<Word> wordList) {
        List<String> errorList = new ArrayList<>();

        if (wordList != null && wordList.stream()
                .anyMatch(w -> StringUtils.isEmpty(w.getWordOriginal()) ||
                        StringUtils.isEmpty(w.getWordTranslation()))) {

            errorList.add(ValidationError.INVALID_WORD.getText());
        }
        return errorList;
    }

    public Integer deleteWordById(Integer id) {
        return pwRequestContext.getCurrentUser()
                .orElseThrow(RuntimeException::new)
                .getId().equals(
                        repository
                                .getUserIdByWordId(id)
                                .orElseThrow(RuntimeException::new)
                ) ? repository.deleteWordById(id) : 0;
    }

    public Optional<Word> updateWord(Word word) {
        final PwUserDetails pwUserDetails = pwRequestContext.getCurrentUser().orElseThrow(RuntimeException::new);

        return pwUserDetails.getId()
                .equals(repository.getWordGroupById(word.getGroupId())
                        .orElseThrow(RuntimeException::new).getUserId()) ? repository.updateWord(word) : Optional.empty();
    }
}

