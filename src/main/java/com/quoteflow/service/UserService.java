package com.quoteflow.service;

import com.quoteflow.dto.RegisterRequest;
import com.quoteflow.entity.BusinessProfile;
import com.quoteflow.entity.User;
import com.quoteflow.repository.BusinessProfileRepository;
import com.quoteflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       BusinessProfileRepository businessProfileRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessProfileRepository = businessProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        User savedUser = userRepository.saveAndFlush(user);

        // Initialize Business Profile for Tenant
        BusinessProfile bp = new BusinessProfile();
        bp.setUser(savedUser);
        bp.setBusinessName(request.getBusinessName().trim());
        bp.setEmail(savedUser.getEmail());
        bp.setPhone(savedUser.getPhone());
        bp.setDefaultTaxPercentage(new BigDecimal("18.00"));
        bp.setQuotationPrefix("QT");
        bp.setQuotationValidityDays(15);
        bp.setTermsAndConditions("1. Payment terms: 50% advance upon confirmation.\n2. Quotation valid for 15 days from issue date.");

        businessProfileRepository.saveAndFlush(bp);

        return savedUser;
    }
}
