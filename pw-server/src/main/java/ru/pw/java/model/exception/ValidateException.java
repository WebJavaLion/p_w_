package ru.pw.java.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Lev_S
 */

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ValidateException extends RuntimeException {

    private List<String> errorList;

    public ValidateException(List<String> errorList) {
        this.errorList = errorList;
    }

    public ValidateException(String message) {
        super(message);
        this.errorList = new ArrayList<>();
        errorList.add(message);
    }

    @Override
    public String getMessage() {
        StringBuilder m = new StringBuilder();

        for (String mes : errorList) {
            m.append(mes).append("\n");
        }

        return m.toString();
    }
}
