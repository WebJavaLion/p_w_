package ru.pw.java.model.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Lev_S
 */

@ResponseStatus
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
        String m = "";

        for (String mes : errorList) {
            m += mes + "\n";
        }

        return m;
    }
}
