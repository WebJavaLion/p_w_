package ru.pw.java.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.pw.java.model.param.UserParam;
import ru.pw.java.model.pojo.PwUserDetails;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.service.MailService;
import ru.pw.java.service.UserService;
import ru.pw.java.tables.pojos.Users;

import java.util.List;
import java.util.Optional;

/**
 * @author Lev_S
 */

@RestController
@RequestMapping(path = "/user")
@Transactional
public class UserController {

    final UserService service;
    final MailService mailService;
    final PwRequestContext pwRequestContext;

    private static final Logger log = LogManager.getLogger(UserController.class);

    public UserController(UserService service,
                          MailService mailService,
                          PwRequestContext pwRequestContext) {
        this.service = service;
        this.mailService = mailService;
        this.pwRequestContext = pwRequestContext;
    }

    @GetMapping
    public List<Users> getAll(@AuthenticationPrincipal PwUserDetails user) {
        return service.getUsers();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Users> getById(@PathVariable Integer id) {
        Optional<Users> optionalUser = service.getById(id);

        return optionalUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/test")
    public void test() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass().getName());
    }

    @PostMapping("/registration")
    public void create(@RequestBody UserParam param,
                       @RequestParam(required = false, defaultValue = "false") Boolean isAdmin) {
        System.out.println(isAdmin);
        service.registration(param.getMail(), param.getPassword(), isAdmin);
    }

}