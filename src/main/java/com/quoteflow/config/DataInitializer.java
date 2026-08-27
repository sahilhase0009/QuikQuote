package com.quoteflow.config;

import com.quoteflow.entity.*;
import com.quoteflow.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final QuotationRepository quotationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           BusinessProfileRepository businessProfileRepository,
                           CustomerRepository customerRepository,
                           ProductRepository productRepository,
                           QuotationRepository quotationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessProfileRepository = businessProfileRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.quotationRepository = quotationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping DataInitializer seeding.");
            return;
        }

        log.info("Seeding demo data for QuikQuote application...");

        // 1. Create Demo User
        User user = new User();
        user.setName("Rahul Patil");
        user.setEmail("demo@primeoffice.com");
        user.setPhone("+91 9876543210");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setEnabled(true);
        user = userRepository.save(user);

        // 2. Create Business Profile
        BusinessProfile bp = new BusinessProfile();
        bp.setUser(user);
        bp.setBusinessName("Prime Office Solutions");
        bp.setLogo("https://images.unsplash.com/photo-1497366216548-37526070297c?w=150");
        bp.setAddress("102 Industrial Estate, MG Road");
        bp.setCity("Mumbai");
        bp.setState("Maharashtra");
        bp.setCountry("India");
        bp.setPincode("400001");
        bp.setPhone("+91 9876543210");
        bp.setEmail("sales@primeoffice.com");
        bp.setWebsite("www.primeoffice.com");
        bp.setGstNumber("27AAAAA0000A1Z5");
        bp.setPanNumber("ABCDE1234F");
        bp.setDefaultTaxPercentage(new BigDecimal("18.00"));
        bp.setQuotationPrefix("QT");
        bp.setQuotationValidityDays(15);
        bp.setBankName("HDFC Bank");
        bp.setBankAccountNumber("50200012345678");
        bp.setIfscCode("HDFC0000123");
        bp.setTermsAndConditions("1. 50% advance payment required upon order confirmation.\n2. Delivery within 7-10 business days.\n3. Quotation valid for 15 days from date of issue.");
        bp = businessProfileRepository.save(bp);

        // 3. Create Customers
        Customer c1 = new Customer();
        c1.setBusiness(bp);
        c1.setName("Amit Sharma");
        c1.setCompanyName("Apex Technologies");
        c1.setPhone("+91 9820011223");
        c1.setEmail("amit@apextech.com");
        c1.setAddress("Building 4, SEEPZ, Andheri East");
        c1.setCity("Mumbai");
        c1.setState("Maharashtra");
        c1.setPincode("400096");
        c1.setNotes("Key corporate account");
        c1 = customerRepository.save(c1);

        Customer c2 = new Customer();
        c2.setBusiness(bp);
        c2.setName("Priya Nair");
        c2.setCompanyName("Design Studio One");
        c2.setPhone("+91 9819988776");
        c2.setEmail("priya@designstudio.com");
        c2.setAddress("Suite 201, Express Towers, Nariman Point");
        c2.setCity("Mumbai");
        c2.setState("Maharashtra");
        c2.setPincode("400021");
        c2.setNotes("Interested in ergonomic furniture");
        c2 = customerRepository.save(c2);

        Customer c3 = new Customer();
        c3.setBusiness(bp);
        c3.setName("Rajesh Kumar");
        c3.setCompanyName("TechVision Systems");
        c3.setPhone("+91 9765432109");
        c3.setEmail("rajesh@techvision.io");
        c3.setAddress("Plot 12, IT Park, Hinjewadi");
        c3.setCity("Pune");
        c3.setState("Maharashtra");
        c3.setPincode("411057");
        c3.setNotes("New branch office setup");
        c3 = customerRepository.save(c3);

        // 4. Create Products
        Product p1 = new Product();
        p1.setBusiness(bp);
        p1.setName("Office Chair");
        p1.setDescription("High-back mesh ergonomic chair with lumbar support and 3D armrests");
        p1.setCategory("Furniture");
        p1.setUnit("Piece");
        p1.setPrice(new BigDecimal("4500.00"));
        p1.setTaxPercentage(new BigDecimal("18.00"));
        p1 = productRepository.save(p1);

        Product p2 = new Product();
        p2.setBusiness(bp);
        p2.setName("Office Table");
        p2.setDescription("Modular executive desk with cable management and drawer pedestal");
        p2.setCategory("Furniture");
        p2.setUnit("Piece");
        p2.setPrice(new BigDecimal("8000.00"));
        p2.setTaxPercentage(new BigDecimal("18.00"));
        p2 = productRepository.save(p2);

        Product p3 = new Product();
        p3.setBusiness(bp);
        p3.setName("Ergonomic Chair");
        p3.setDescription("Premium ergonomic chair with synchronized tilt mechanism");
        p3.setCategory("Furniture");
        p3.setUnit("Piece");
        p3.setPrice(new BigDecimal("7500.00"));
        p3.setTaxPercentage(new BigDecimal("18.00"));
        p3 = productRepository.save(p3);

        Product p4 = new Product();
        p4.setBusiness(bp);
        p4.setName("Laptop Stand");
        p4.setDescription("Aluminum adjustable laptop riser stand with cooling ventilation");
        p4.setCategory("Accessories");
        p4.setUnit("Piece");
        p4.setPrice(new BigDecimal("2000.00"));
        p4.setTaxPercentage(new BigDecimal("18.00"));
        p4 = productRepository.save(p4);

        // 5. Create Sample Quotation 1 (SENT)
        Quotation q1 = new Quotation();
        q1.setBusiness(bp);
        q1.setCustomer(c1);
        q1.setQuotationNumber("QT-2026-0001");
        q1.setQuotationDate(LocalDate.now());
        q1.setValidUntil(LocalDate.now().plusDays(15));
        q1.setStatus(QuotationStatus.SENT);
        q1.setSubtotal(new BigDecimal("85000.00"));
        q1.setDiscountType(DiscountType.FIXED);
        q1.setDiscountValue(new BigDecimal("5000.00"));
        q1.setDiscountAmount(new BigDecimal("5000.00"));
        q1.setTaxAmount(new BigDecimal("14400.00"));
        q1.setGrandTotal(new BigDecimal("94400.00"));
        q1.setNotes("Delivery included to Andheri East office.");
        q1.setTermsAndConditions(bp.getTermsAndConditions());

        QuotationItem item1 = new QuotationItem();
        item1.setProduct(p1);
        item1.setItemName(p1.getName());
        item1.setDescription(p1.getDescription());
        item1.setQuantity(10);
        item1.setUnit(p1.getUnit());
        item1.setUnitPrice(p1.getPrice());
        item1.setTaxPercentage(p1.getTaxPercentage());
        item1.setLineSubtotal(new BigDecimal("45000.00"));
        item1.setLineTax(new BigDecimal("8100.00"));
        item1.setLineTotal(new BigDecimal("53100.00"));
        q1.addItem(item1);

        QuotationItem item2 = new QuotationItem();
        item2.setProduct(p2);
        item2.setItemName(p2.getName());
        item2.setDescription(p2.getDescription());
        item2.setQuantity(5);
        item2.setUnit(p2.getUnit());
        item2.setUnitPrice(p2.getPrice());
        item2.setTaxPercentage(p2.getTaxPercentage());
        item2.setLineSubtotal(new BigDecimal("40000.00"));
        item2.setLineTax(new BigDecimal("7200.00"));
        item2.setLineTotal(new BigDecimal("47200.00"));
        q1.addItem(item2);

        quotationRepository.save(q1);

        // 6. Create Sample Quotation 2 (ACCEPTED)
        Quotation q2 = new Quotation();
        q2.setBusiness(bp);
        q2.setCustomer(c2);
        q2.setQuotationNumber("QT-2026-0002");
        q2.setQuotationDate(LocalDate.now().minusDays(5));
        q2.setValidUntil(LocalDate.now().plusDays(10));
        q2.setStatus(QuotationStatus.ACCEPTED);
        q2.setSubtotal(new BigDecimal("150000.00"));
        q2.setDiscountType(DiscountType.PERCENTAGE);
        q2.setDiscountValue(new BigDecimal("10.00"));
        q2.setDiscountAmount(new BigDecimal("15000.00"));
        q2.setTaxAmount(new BigDecimal("24300.00"));
        q2.setGrandTotal(new BigDecimal("159300.00"));
        q2.setNotes("Boardroom ergonomic chair order for Design Studio One.");
        q2.setTermsAndConditions(bp.getTermsAndConditions());

        QuotationItem item3 = new QuotationItem();
        item3.setProduct(p3);
        item3.setItemName(p3.getName());
        item3.setDescription(p3.getDescription());
        item3.setQuantity(20);
        item3.setUnit(p3.getUnit());
        item3.setUnitPrice(p3.getPrice());
        item3.setTaxPercentage(p3.getTaxPercentage());
        item3.setLineSubtotal(new BigDecimal("150000.00"));
        item3.setLineTax(new BigDecimal("27000.00"));
        item3.setLineTotal(new BigDecimal("177000.00"));
        q2.addItem(item3);

        quotationRepository.save(q2);

        // 7. Create Sample Quotation 3 (DRAFT)
        Quotation q3 = new Quotation();
        q3.setBusiness(bp);
        q3.setCustomer(c3);
        q3.setQuotationNumber("QT-2026-0003");
        q3.setQuotationDate(LocalDate.now());
        q3.setValidUntil(LocalDate.now().plusDays(15));
        q3.setStatus(QuotationStatus.DRAFT);
        q3.setSubtotal(new BigDecimal("20000.00"));
        q3.setDiscountType(DiscountType.PERCENTAGE);
        q3.setDiscountValue(BigDecimal.ZERO);
        q3.setDiscountAmount(BigDecimal.ZERO);
        q3.setTaxAmount(new BigDecimal("3600.00"));
        q3.setGrandTotal(new BigDecimal("23600.00"));
        q3.setNotes("Draft quotation for tech vision stand order.");
        q3.setTermsAndConditions(bp.getTermsAndConditions());

        QuotationItem item4 = new QuotationItem();
        item4.setProduct(p4);
        item4.setItemName(p4.getName());
        item4.setDescription(p4.getDescription());
        item4.setQuantity(10);
        item4.setUnit(p4.getUnit());
        item4.setUnitPrice(p4.getPrice());
        item4.setTaxPercentage(p4.getTaxPercentage());
        item4.setLineSubtotal(new BigDecimal("20000.00"));
        item4.setLineTax(new BigDecimal("3600.00"));
        item4.setLineTotal(new BigDecimal("23600.00"));
        q3.addItem(item4);

        quotationRepository.save(q3);

        log.info("Demo data seeding completed successfully! Login with demo@primeoffice.com / password123");
    }
}
