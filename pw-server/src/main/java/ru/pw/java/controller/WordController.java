package ru.pw.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pw.java.Tables;
import ru.pw.java.model.pojo.PwUserDetails;
import ru.pw.java.model.pojo.WordGroupPojo;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.repository.WordRepository;
import ru.pw.java.service.NotificationService;
import ru.pw.java.service.WordService;
import ru.pw.java.tables.pojos.Users;
import ru.pw.java.tables.pojos.Word;
import ru.pw.java.tables.pojos.WordGroup;
import ru.pw.java.tables.records.WordRecord;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

/**
 * @author Lev_S
 */

@RestController
@RequestMapping("/word")
@Transactional
public class WordController {

    private final DSLContext context;
    private final WordService service;
    private final WordRepository repository;
    private final WordRepository wordRepository;
    private final NotificationService notificationService;

    public WordController(DSLContext context,
                          WordService service,
                          WordRepository repository,
                          WordRepository wordRepository,
                          NotificationService notificationService) {

        this.context = context;
        this.service = service;
        this.repository = repository;
        this.wordRepository = wordRepository;
        this.notificationService = notificationService;
    }


    @GetMapping
    public Map<Integer, List<WordGroup>> getWordGroupList() {
        final Map<Integer, List<WordGroup>> integerListMap = wordRepository.get();
        System.out.println(integerListMap);
        ResponseEntity<? extends WordGroup> f;
        ResponseEntity<WordGroup> r = null;

        return integerListMap;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<? extends WordGroup> getWordGroupById(@PathVariable Integer id,
                                                                @AuthenticationPrincipal PwUserDetails user) {

        Optional<WordGroupPojo> group = service.getGroupByEntityId(id);
        return group.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping
    public ResponseEntity<Word> updateWord(@RequestBody Word word) {
        return service
                .updateWord(word)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/group/file")
    public Integer word(@RequestParam("file") MultipartFile file) throws IOException {
        return service.uploadFileWithWords(file);
    }

    @PostMapping("/group")
    public void createWordGroup(@RequestBody WordGroupPojo pojo,
                                @AuthenticationPrincipal Users user) throws IOException {
        service.createWordGroup(pojo);
//        pojo.setUserId(user.getId());
//        pojo.setMixingModeId(1);
//        pojo.setName("Jopa");
//        pojo.setCreatedDate(Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES)));
//
//        context.insertInto(Tables.WORD_GROUP)
//                .set(context.newRecord(Tables.WORD_GROUP, pojo))
//                .execute();
    }

    @PostMapping
    public Word createWord(@RequestBody Word word) {
        final Word word1 = repository.createWord(word);
        System.out.println(word1);
        return word1;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWordById(@PathVariable("id") Integer id) {
       // return ResponseEntity.badRequest().build();
        return service.deleteWordById(id) != 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("/group")
    public void updateWordGroup(@RequestBody WordGroupPojo pojo) throws IOException {
        service.updateWordGroup(pojo);
    }

}
