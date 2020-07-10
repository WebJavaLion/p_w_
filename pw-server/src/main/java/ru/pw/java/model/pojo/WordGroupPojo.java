package ru.pw.java.model.pojo;

import lombok.*;
import ru.pw.java.tables.pojos.Word;
import ru.pw.java.tables.pojos.WordGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lev_S
 */


@AllArgsConstructor
@NoArgsConstructor
public class WordGroupPojo extends WordGroup {

    private Long tempFileId;
    private List<Word> wordList = new ArrayList<>();


    public WordGroupPojo(WordGroup value) {
        super(value);
    }
    public WordGroupPojo(WordGroup wordGroup, List<Word> words) {
        super(wordGroup);
        wordList = words;
    }

    public Long getTempFileId() {
        return tempFileId;
    }

    public void setTempFileId(Long tempFileId) {
        this.tempFileId = tempFileId;
    }

    public List<Word> getWordList() {
        return wordList;
    }

    public void setWordList(List<Word> wordList) {
        this.wordList = wordList;
    }
}
