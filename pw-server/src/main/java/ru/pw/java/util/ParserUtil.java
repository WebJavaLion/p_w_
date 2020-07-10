package ru.pw.java.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.pw.java.model.exception.ValidateException;
import ru.pw.java.tables.pojos.Word;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lev_S
 */

public abstract class ParserUtil {

    private ParserUtil() {}

    public static List<Word> parseExcel(byte[] bytes) throws IOException {
        List<Word> result = new ArrayList<>();

        try (InputStream stream = new ByteArrayInputStream(bytes)) {
            XSSFWorkbook wb = new XSSFWorkbook(stream);

            Sheet sheet = wb.getSheetAt(0);

            int i = 0;
            Iterator<Row> iterator = sheet.rowIterator();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                Iterator<Cell> cellIterator = row.iterator();

                Word word = new Word();
                while (cellIterator.hasNext()) {

                    String w = cellIterator.next().toString();

                    if (i > 1) {
                        break;
                    } else if (w.isEmpty()) {
                        throw new ValidateException("Ошибка данных");
                    }

                    if (i == 0) {
                        word.setWordOriginal(w);
                    } else if (i == 1) {
                        word.setWordTranslation(w);
                    }
                    i++;
                }
                result.add(word);
                i = 0;
            }
        }
        return result;
    }
}
