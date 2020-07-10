package ru.pw.java.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import ru.pw.java.model.pojo.LoginRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    final ObjectMapper mapper;

    public AuthFilter(ObjectMapper mapper, AuthenticationManager manager) {
        this.mapper = mapper;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
        setAuthenticationManager(manager);
        setAuthenticationSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
            httpServletResponse.setStatus(200);
            httpServletResponse.getOutputStream().println("Success");
        });
        setFilterProcessesUrl("/login");
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        try {
            final String collect = request.getReader().lines().collect(Collectors.joining());
            final LoginRequest loginRequest = mapper.readValue(collect, LoginRequest.class);

            if (StringUtils.isEmpty(loginRequest.getUsername()) || StringUtils.isEmpty(loginRequest.getPassword())) {
                throw new BadCredentialsException("bad credentials");
            }
            final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    );

            setDetails(request, usernamePasswordAuthenticationToken);

            return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
        } catch (Exception e) {
            throw new BadCredentialsException("bad credentials");
        }
    }
}
