package ru.pw.telegram.java.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pw.telegram.java.model.param.RepeatedWordsPojo;

import java.util.List;

/**
 * @author Lev_S
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect
public class CurrentRepeatedDto {

   List<RepeatedWordsPojo> repeatedWordsList;

}
