package ru.pw.java.model.shared;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import ru.pw.java.tables.pojos.Users;

import java.io.Serializable;

/**
 * @author Lev_S
 */

@Component
@RequestScope
@Getter
@Setter
public class PwRequestContext {


    private Users currentUser;

}
