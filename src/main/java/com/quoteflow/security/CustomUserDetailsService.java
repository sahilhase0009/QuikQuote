package com.quoteflow.security;

import com.quoteflow.entity.BusinessProfile;
import com.quoteflow.entity.User;
import com.quoteflow.repository.BusinessProfileRepository;
import com.quoteflow.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BusinessProfileRepository businessProfileRepository;

    public CustomUserDetailsService(UserRepository userRepository, BusinessProfileRepository businessProfileRepository) {
        this.userRepository = userRepository;
        this.businessProfileRepository = businessProfileRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String cleanEmail = email != null ? email.toLowerCase().trim() : "";
        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        BusinessProfile businessProfile = businessProfileRepository.findByUserId(user.getId())
                .orElse(null);

        return new CustomUserDetails(user, businessProfile);
    }
}
