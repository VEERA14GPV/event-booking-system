package com.booking.security;

import com.booking.entity.User;

import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/*
 * UserPrincipal acts as a bridge between
 * our application User entity and Spring Security.
 *
 * Spring Security requires a UserDetails object
 * for authentication and authorization.
 */
public class UserPrincipal implements UserDetails {

    private static final long serialVersionUID = 1L;

    /*
     * User ID from database.
     * Used in rate limiting and other business logic.
     */
    private Long id;

    /*
     * Username used for login.
     */
    private String username;

    /*
     * Encrypted password stored in database.
     */
    private String password;

    /*
     * User roles/permissions.
     *
     * Example:
     * ROLE_USER
     * ROLE_ADMIN
     */
    private Collection<? extends GrantedAuthority> authorities;

    /*
     * Constructor used to create UserPrincipal.
     */
    public UserPrincipal(
            Long id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities) {

        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    /*
     * Converts application User entity
     * into Spring Security UserPrincipal.
     *
     * Flow:
     *
     * User Entity
     *      ↓
     * UserPrincipal
     */
    public static UserPrincipal create(User user) {

        /*
         * Convert application role
         * into Spring Security authority.
         */
        GrantedAuthority authority =
                new SimpleGrantedAuthority(
                        user.getRole()
                                .getName()
                                .name()
                );

        /*
         * Create UserPrincipal object.
         */
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }

    /*
     * Returns user ID.
     *
     * Used by:
     * - RateLimitingFilter
     * - Business logic
     */
    public Long getId() {
        return id;
    }

    /*
     * Returns user roles.
     *
     * Used by Spring Security
     * for authorization.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /*
     * Returns encrypted password.
     *
     * Used during authentication.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /*
     * Returns username.
     *
     * Used during login and JWT validation.
     */
    @Override
    public String getUsername() {
        return username;

    }
}