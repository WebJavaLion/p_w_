package ru.pw.java.security;

import org.springframework.beans.factory.annotation.Autowired;
import ru.pw.java.model.enums.FreeApi;
import ru.pw.java.repository.UserRepository;
import ru.pw.java.model.shared.PwRequestContext;
import ru.pw.java.tables.pojos.Users;
import ru.pw.java.util.SecurityUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * @author Lev_S
 */

@WebFilter(urlPatterns = "/*")
public class SecurityFilter implements Filter {

    @Autowired
    PwRequestContext pwRequestContext;

    @Autowired
    UserRepository repository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        final String path = request.getServletPath();

        if (Arrays.asList(
                FreeApi.LOGIN.getName(),
                FreeApi.AUTH_TELEGRAM.getName(),
                FreeApi.REGISTRATION.getName(),
                FreeApi.CHECK_AUTH_TELEGRAM.getName()).contains(path)) {
            chain.doFilter(request, response);
        } else {
            Cookie userCookie = null;

            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("userId")) {
                        userCookie = cookie;
                    }
                 //   if (request.getCookies().length > 1) {
                       // ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_FORBIDDEN);
                   // }
                }
            }

            if (userCookie != null) {
                String userId = SecurityUtil.decode(userCookie.getValue());

                if (!isEmpty(userId)) {
                    Optional<Users> optionalUsers = repository.getUserById(Integer.parseInt(userId));

                    if (optionalUsers.isPresent()) {
                        pwRequestContext.setCurrentUser(optionalUsers.get());
                        chain.doFilter(request, response);
                    } else {
                        ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                } else {
                    ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    @Override
    public void destroy() {
    }
}
