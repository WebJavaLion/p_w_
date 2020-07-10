package ru.pw.java.model.pojo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.pw.java.enums.PwRoles;
import ru.pw.java.tables.pojos.Users;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PwUserDetails extends Users implements UserDetails {

    Collection<? extends GrantedAuthority> authorities;

    public PwUserDetails(Users user) {
        super(user);
    }

    public PwUserDetails(Users user,
                         Function<? super Integer, ? extends List<PwRoles>> rolesFunction) {
        super(user);
        authorities = rolesFunction.apply(user.getId())
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getLiteral()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return getUserPassword();
    }

    @Override
    public String getUsername() {
        return getUserEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
