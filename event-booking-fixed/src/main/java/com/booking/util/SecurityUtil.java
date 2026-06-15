package com.booking.util;

import com.booking.security.UserPrincipal;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.
        SecurityContextHolder;

import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    /*
     * Get current authenticated user
     */
    public UserPrincipal getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal()
                instanceof UserPrincipal)) {

            return null;
        }

        return (UserPrincipal)
                authentication.getPrincipal();
    }

    /*
     * Get authenticated user id
     */
    public Long getCurrentUserId() {

        UserPrincipal user =
                getCurrentUser();

        return user != null
                ? user.getId()
                : null;
    }
    
    /*
     * Check role
     */
    public boolean hasRole(
            String role) {

        UserPrincipal user =
                getCurrentUser();

        if (user == null) {
            return false;
        }

        return user.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals(role)
                );
    }
}