package ru.pw.java.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.pw.java.model.enums.FreeApi;
import ru.pw.java.security.AuthFilter;
import ru.pw.java.security.FailureHandler;
import ru.pw.java.service.PwUserDetailsService;

import java.util.Arrays;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    final PwUserDetailsService userDetailsService;
    final ObjectMapper objectMapper;

    @Autowired
    public WebSecurityConfig(PwUserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);

        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(
                        Arrays.stream(FreeApi.values())
                                .map(FreeApi::getName)
                                .toArray(String[]::new)
                )
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(getAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(
                        (httpServletRequest, httpServletResponse, e) -> {
                            System.out.println(e.getMessage());
                            httpServletResponse.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
                        })
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                //.addFilterAfter(new CustomChainFilter("BEFORE PROCESSING"), SecurityContextPersistenceFilter.class)
//                .formLogin().usernameParameter("username").passwordParameter("password").loginProcessingUrl("/login")
//                .loginPage("/")
//                .failureHandler(failureHandler())
//                .successForwardUrl("/")
                .logout()
                .clearAuthentication(true)
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .csrf()
                .disable();
    }

    @SneakyThrows
    @Bean
    AuthFilter getAuthFilter() {
        return new AuthFilter(objectMapper, manager());
    }

    @Bean
    AuthenticationFailureHandler failureHandler() {
        return new FailureHandler();
    }

    @Bean
    AuthenticationManager manager() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
