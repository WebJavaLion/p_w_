package ru.pw.java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.pw.java.enums.PwRoles;
import ru.pw.java.model.pojo.PwUserDetails;
import ru.pw.java.repository.UserRepository;
import ru.pw.java.tables.pojos.Users;

import java.util.List;
import java.util.Optional;

@Service
public class PwUserDetailsService implements UserDetailsService {

    final UserRepository repository;

    @Autowired
    PwUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public PwUserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        final Optional<Users> users = repository.loadUserByUsername(s);

        return new PwUserDetails(
                users.orElseThrow(() -> new UsernameNotFoundException("User not found")),
                id -> {
                    final List<PwRoles> rolesByUserId = repository.getRolesByUserId(id);

                    if (rolesByUserId.isEmpty()) {
                        throw new UsernameNotFoundException("User not found");
                    }
                    return rolesByUserId;
                });
    }
}

