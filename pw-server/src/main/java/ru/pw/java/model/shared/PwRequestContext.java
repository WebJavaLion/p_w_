package ru.pw.java.model.shared;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import ru.pw.java.model.pojo.PwUserDetails;

import java.util.Optional;

/**
 * @author Lev_S
 */

@Component
@RequestScope
public class PwRequestContext {

    public Optional<PwUserDetails> getCurrentUser() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof PwUserDetails) {
            return Optional.of((PwUserDetails) principal);
        }
        return Optional.empty();
    }
}
