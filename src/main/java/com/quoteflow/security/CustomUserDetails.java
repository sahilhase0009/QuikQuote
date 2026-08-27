package com.quoteflow.security;

import com.quoteflow.entity.BusinessProfile;
import com.quoteflow.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final BusinessProfile businessProfile;

    public CustomUserDetails(User user, BusinessProfile businessProfile) {
        this.user = user;
        this.businessProfile = businessProfile;
    }

    public User getUser() {
        return user;
    }

    public BusinessProfile getBusinessProfile() {
        return businessProfile;
    }

    public Long getBusinessId() {
        return businessProfile != null ? businessProfile.getId() : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
        return user.isEnabled();
    }
}
