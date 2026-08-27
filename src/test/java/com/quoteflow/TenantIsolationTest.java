package com.quoteflow;

import com.quoteflow.dto.*;
import com.quoteflow.entity.QuotationStatus;
import com.quoteflow.exception.ResourceNotFoundException;
import com.quoteflow.security.CustomUserDetails;
import com.quoteflow.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TenantIsolationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private UserDetailsService userDetailsService;

    private Long businessACustomerId;
    private Long businessAProductId;
    private Long businessAQuotationId;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        // 1. Register Business A
        RegisterRequest reqA = new RegisterRequest();
        reqA.setName("Business A Admin");
        reqA.setEmail("admin@businessA.com");
        reqA.setPassword("passwordA123");
        reqA.setBusinessName("Business A Corp");
        userService.registerUser(reqA);

        // Authenticate as Business A
        authenticateUser("admin@businessA.com");

        // Create Customer for Business A
        CustomerDto cA = new CustomerDto();
        cA.setName("Customer A");
        cA.setEmail("custA@client.com");
        CustomerDto createdCA = customerService.createCustomer(cA);
        businessACustomerId = createdCA.getId();

        // Create Product for Business A
        ProductDto pA = new ProductDto();
        pA.setName("Product A");
        pA.setPrice(new BigDecimal("1000.00"));
        pA.setTaxPercentage(new BigDecimal("18.00"));
        ProductDto createdPA = productService.createProduct(pA);
        businessAProductId = createdPA.getId();

        // Create Quotation for Business A
        QuotationDto qA = new QuotationDto();
        qA.setCustomerId(businessACustomerId);
        qA.setQuotationDate(LocalDate.now());
        
        QuotationItemDto itemA = new QuotationItemDto();
        itemA.setProductId(businessAProductId);
        itemA.setItemName("Product A");
        itemA.setQuantity(2);
        itemA.setUnitPrice(new BigDecimal("1000.00"));
        itemA.setTaxPercentage(new BigDecimal("18.00"));
        qA.setItems(List.of(itemA));

        QuotationDto createdQA = quotationService.createQuotation(qA);
        businessAQuotationId = createdQA.getId();

        // 2. Register Business B
        RegisterRequest reqB = new RegisterRequest();
        reqB.setName("Business B Admin");
        reqB.setEmail("admin@businessB.com");
        reqB.setPassword("passwordB123");
        reqB.setBusinessName("Business B Enterprise");
        userService.registerUser(reqB);

        SecurityContextHolder.clearContext();
    }

    private void authenticateUser(String email) {
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    @DisplayName("Tenant Isolation: Business B cannot access Business A's customer")
    void testCustomerIsolation() {
        // Authenticate as Business B
        authenticateUser("admin@businessB.com");

        // Business B listing customers should NOT contain Business A's customer
        List<CustomerDto> bCustomers = customerService.getAllCustomers();
        assertTrue(bCustomers.stream().noneMatch(c -> c.getId().equals(businessACustomerId)));

        // Direct fetch by ID should throw ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerDtoById(businessACustomerId));
    }

    @Test
    @DisplayName("Tenant Isolation: Business B cannot access Business A's product")
    void testProductIsolation() {
        // Authenticate as Business B
        authenticateUser("admin@businessB.com");

        List<ProductDto> bProducts = productService.getAllProducts();
        assertTrue(bProducts.stream().noneMatch(p -> p.getId().equals(businessAProductId)));

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductDtoById(businessAProductId));
    }

    @Test
    @DisplayName("Tenant Isolation: Business B cannot access or edit Business A's quotation")
    void testQuotationIsolation() {
        // Authenticate as Business B
        authenticateUser("admin@businessB.com");

        List<QuotationDto> bQuotations = quotationService.getAllQuotations(null, null);
        assertTrue(bQuotations.stream().noneMatch(q -> q.getId().equals(businessAQuotationId)));

        // Direct access by ID
        assertThrows(ResourceNotFoundException.class, () -> quotationService.getQuotationDtoById(businessAQuotationId));

        // Direct duplication attempt
        assertThrows(ResourceNotFoundException.class, () -> quotationService.duplicateQuotation(businessAQuotationId));

        // Direct status update attempt
        assertThrows(ResourceNotFoundException.class, () -> quotationService.updateStatus(businessAQuotationId, QuotationStatus.ACCEPTED));
    }
}
