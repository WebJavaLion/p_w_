package ru.pw.java.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import ru.pw.java.enums.PwRoles;
import ru.pw.java.model.enums.ValidationError;
import ru.pw.java.model.exception.ValidateException;
import ru.pw.java.model.pojo.PwUserDetails;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.repository.UserRepository;
import ru.pw.java.tables.pojos.Users;
import ru.pw.java.util.TelegramCodeUtil;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Lev_S
 */

@Service
public class UserService {

    private final MailService mailService;
    private final UserRepository repository;
    private final PwRequestContext pwRequestContext;


    private final static String MAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String PASSWORD_PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";

    private static final Logger log = LogManager.getLogger(UserService.class);

    public UserService(MailService mailService,
                       UserRepository repository,
                       PwRequestContext pwRequestContext) {

        this.repository = repository;
        this.mailService = mailService;
        this.pwRequestContext = pwRequestContext;
    }

    public Optional<Users> getById(Integer id) {
        return repository.getUserById(id);
    }

    public List<Users> getUsers() {
        return repository.getAllUsers();
    }

    public void registration(String mail, String password, boolean isAdmin) {
        validateRegistration(mail, password);

        Users user = repository.createUser(mail, password);
        final Optional<PwUserDetails> currentUser = pwRequestContext.getCurrentUser();

        if (currentUser.isPresent() && currentUser.get().getAuthorities()
                        .contains(new SimpleGrantedAuthority(PwRoles.ADMIN.getLiteral()))) {
            setAuthoritiesForUser(user.getId(), isAdmin);
        } else {
            setAuthoritiesForUser(user.getId(), false);
        }
        // mailService.sendRegisteredEmail(user);
    }

    private void setAuthoritiesForUser(Integer id, boolean isAdmin) {
        repository.setRolesForUser(id, isAdmin);
    }

    private void validateRegistration(String mail, String password) {
        List<String> errorList = Stream
                .of(
                        validateMail(mail),
                        validatePassword(password)
                )
                .flatMap(List::stream)
                .collect(Collectors.toList());

        if (!errorList.isEmpty()) {
            log.error("Failed registration");

            throw new ValidateException(errorList);
        }
    }

    private List<String> validateMail(String mail) {
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
        return errorList;
    }

    private List<String> validatePassword(String password) {
        List<String> errorList = new ArrayList<>();

        if (isEmpty(password)) {
            errorList.add(ValidationError.EMPTY_PASSWORD.getText());
        } else {
            if (!Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()) {
                errorList.add(ValidationError.INVALID_PASSWORD.getText());
            }
        }
        return errorList;
    }
}
