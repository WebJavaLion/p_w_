package ru.pw.java.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.pw.java.model.enums.ValidationError;
import ru.pw.java.model.exception.ValidateException;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.repository.UserRepository;
import ru.pw.java.tables.pojos.Users;
import ru.pw.java.util.TelegramCodeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Lev_S
 */

@Service
public class UserService {


    @Autowired
    UserRepository repository;

    @Autowired
    MailService mailService;

    @Autowired
    PwRequestContext pwRequestContext;

    private final static String MAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private final static String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";

    private static final Logger log = LogManager.getLogger(UserService.class);


    public Optional<Users> getById(Integer id) {
        Optional<Users> userOptional = repository.getUserById(id);

        if (userOptional.isPresent()) {
            return Optional.of(userOptional.get());
        } else {
            return Optional.empty();
        }
    }

    public List<Users> getUsers(){
        return repository.getAllUsers();
    }

    public Optional<Users> login(String mail, String password) {
        validateLogin(mail, password);

        return repository.getUserByEmailAndPassword(mail, password);
    }

    private void validateLogin(String mail, String password) {
        List<String> errorList = new ArrayList<>();

        if (isEmpty(mail)) {
            errorList.add(ValidationError.EMPTY_EMAIL.getText());
        }

        if (isEmpty(password)) {
            errorList.add(ValidationError.EMPTY_PASSWORD.getText());
        }

        if (!errorList.isEmpty()) {
            throw new ValidateException(errorList);
        }
    }

    public void registration(String mail, String password) {
        validateRegistration(mail, password);

        Users user = repository.createUser(mail, password);
        mailService.sendRegisteredEmail(user);
    }

    private void validateRegistration(String mail, String password) {
        List<String> errorList = new ArrayList<>();

        if (isEmpty(mail)) {
            errorList.add(ValidationError.EMPTY_EMAIL.getText());
        } else {
            if (Pattern.compile(MAIL_PATTERN).matcher(mail).matches()) {
                Optional<Users> optionalUsers = repository.getUserByEmail(mail);
                optionalUsers.ifPresent(users -> errorList.add(ValidationError.ALREADY_EXISTS.getText()));
            } else {
                errorList.add(ValidationError.INVALID_EMAIL.getText());
            }
        }

        if (isEmpty(password)) {
            errorList.add(ValidationError.EMPTY_PASSWORD.getText());
        } else {
            if (!Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()) {
                errorList.add(ValidationError.INVALID_PASSWORD.getText());
            }
        }

        if (!errorList.isEmpty()) {
            log.error("Failed registration");

            throw new ValidateException(errorList);
        }
    }
}
