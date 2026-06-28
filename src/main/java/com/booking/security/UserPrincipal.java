package com.booking.security;

import com.booking.entity.User;

import org.springframework.security.core.*;
import org.springframework.security.core.authority.
        SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.
        UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

	    private static final long serialVersionUID = 1L;

	private Long id;

    private String username;

    private String password;

    private Collection<? extends GrantedAuthority>authorities;

    public UserPrincipal(
            Long id,
            String username,
            String password,
            Collection<? extends GrantedAuthority>
                    authorities) {

        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(
            User user) {

        GrantedAuthority authority =
                new SimpleGrantedAuthority(
                        user.getRole()
                                .getName()
                                .name()
                );

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority>
    getAuthorities() {

        return authorities;
    }

    @Override
    public String getPassword() {

        return password;
    }

    @Override
    public String getUsername() {

        return username;
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