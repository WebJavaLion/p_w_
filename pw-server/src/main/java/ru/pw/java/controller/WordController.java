package ru.pw.java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pw.java.model.pojo.WordGroupPojo;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.repository.WordRepository;
import ru.pw.java.service.NotificationService;
import ru.pw.java.service.WordService;
import ru.pw.java.tables.pojos.WordGroup;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Lev_S
 */

@RestController
@RequestMapping("/word")
public class WordController {

    @Autowired
    WordRepository repository;

    @Autowired
    WordService service;

    @Autowired
    NotificationService notificationService;

    @Autowired
    PwRequestContext pwRequestContext;

    @GetMapping(value = "/group/list")
    public List<WordGroup> getWordGroupList() {
        return service.getCurrentUserGroups();
    }

    @GetMapping(value = "/group/{entityId}")
    public ResponseEntity getWordGroupById(@PathVariable Integer entityId) {
        Optional<WordGroupPojo> group = service.getGroupByEntityId(entityId);

        if (group.isPresent()) {
            return ResponseEntity.ok(group.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Transactional
    @PostMapping("/group/file/upload")
    public Integer word(@RequestParam("file") MultipartFile file) throws IOException {
        return service.uploadFileWithWords(file);
    }

    @Transactional
    @PostMapping("/group/create")
    public void createWordGroup(@RequestBody WordGroupPojo pojo) throws IOException {
        service.createWordGroup(pojo);
    }

    @Transactional
    @PostMapping("/group/update")
    public void updateWordGroup(@RequestBody WordGroupPojo pojo) throws IOException {
        service.updateWordGroup(pojo);
    }
}
