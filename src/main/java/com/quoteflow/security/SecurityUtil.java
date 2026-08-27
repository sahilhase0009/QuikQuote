package com.quoteflow.security;

import com.quoteflow.entity.BusinessProfile;
import com.quoteflow.entity.User;
import com.quoteflow.exception.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails;
        }
        throw new AccessDeniedException("User is not authenticated");
    }

    public User getCurrentUser() {
        return getCurrentUserDetails().getUser();
    }

    public BusinessProfile getCurrentBusinessProfile() {
        BusinessProfile bp = getCurrentUserDetails().getBusinessProfile();
        if (bp == null) {
            throw new AccessDeniedException("No business profile associated with current user");
        }
        return bp;
    }

    public Long getCurrentBusinessId() {
        return getCurrentBusinessProfile().getId();
    }
}
