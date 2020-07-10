package ru.pw.java.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException, ServletException {

        httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        httpServletResponse.getOutputStream().println("FUCK_YOU");
    }
}
