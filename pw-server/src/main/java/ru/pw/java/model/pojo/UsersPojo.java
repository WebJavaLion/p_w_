package ru.pw.java.model.pojo;

import ru.pw.java.tables.pojos.Users;
import ru.pw.java.tables.pojos.WordGroup;

import java.util.List;

public class UsersPojo extends Users {

    List<WordGroup> wordGroups;

    public UsersPojo(Users user, List<WordGroup> wordGroups) {
        super(user);
        this.wordGroups = wordGroups;
    }


}
