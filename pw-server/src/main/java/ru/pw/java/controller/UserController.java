package ru.pw.java.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.pw.java.model.param.UserParam;
import ru.pw.java.service.MailService;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.service.UserService;
import ru.pw.java.tables.pojos.Users;
import ru.pw.java.util.SecurityUtil;
import ru.pw.java.util.TelegramCodeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * @author Lev_S
 */

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    UserService service;

    @Autowired
    MailService mailService;

    @Autowired
    PwRequestContext pwRequestContext;

    private static final Logger log = LogManager.getLogger(UserController.class.getName());

    @GetMapping(value = "/all")
    public List<Users> getAll() {
        return service.getUsers();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getById(@PathVariable Integer id) {
        Optional<Users> optionalUser = service.getById(id);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setUserPassword(SecurityUtil.decode(user.getUserPassword()));

            return ResponseEntity.ok(optionalUser.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Transactional
    @PostMapping("/registration")
    public void create(@RequestBody UserParam param) {
        service.registration(param.getMail(), param.getPassword());
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserParam param, HttpServletResponse response) {
        Optional<Users> user = service.login(param.getMail(), param.getPassword());

        if (user.isPresent()) {
            response.addCookie(SecurityUtil.createCookie(user.get().getId()));
            log.info("user with id: " + user.get().getId() + " has entered");

            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/quit")
    public void quit(HttpServletRequest request, HttpServletResponse response) {
        SecurityUtil.dropCookie(request.getCookies(), response);
    }
}