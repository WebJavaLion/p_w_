package ru.pw.telegram.java.model.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Lev_S
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepeatedWordsPojo {

    String wordOriginal;
    String wordTranslated;
}
