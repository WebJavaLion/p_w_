package ru.pw.java.security;

import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import java.io.IOException;


public class CustomChainFilter implements Filter {

    String name;
    public CustomChainFilter(String s) {
        name = s;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        System.out.println(name);
        System.out.println(SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
