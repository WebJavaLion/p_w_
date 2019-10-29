package ru.pw.java.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.pw.java.tables.pojos.Word;
import ru.pw.java.tables.pojos.WordGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lev_S
 */

@Getter
@Setter
@AllArgsConstructor
public class WordGroupPojo extends WordGroup {

    private Long tempFileId;
    private List<Word> wordList = new ArrayList<>();

    public WordGroupPojo(WordGroup value) {
        super(value);
    }
}
