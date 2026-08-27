package com.quoteflow.service;

import com.quoteflow.dto.CustomerDto;
import com.quoteflow.entity.BusinessProfile;
import com.quoteflow.entity.Customer;
import com.quoteflow.exception.ResourceNotFoundException;
import com.quoteflow.repository.CustomerRepository;
import com.quoteflow.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final SecurityUtil securityUtil;

    public CustomerService(CustomerRepository customerRepository, SecurityUtil securityUtil) {
        this.customerRepository = customerRepository;
        this.securityUtil = securityUtil;
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        Long businessId = securityUtil.getCurrentBusinessId();
        return customerRepository.findByBusinessIdOrderByCreatedAtDesc(businessId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> searchCustomers(String query) {
        Long businessId = securityUtil.getCurrentBusinessId();
        if (query == null || query.isBlank()) {
            return getAllCustomers();
        }
        return customerRepository.searchCustomers(businessId, query.trim())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerDto getCustomerDtoById(Long id) {
        Customer c = getCustomerEntityById(id);
        return mapToDto(c);
    }

    @Transactional(readOnly = true)
    public Customer getCustomerEntityById(Long id) {
        Long businessId = securityUtil.getCurrentBusinessId();
        return customerRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
    }

    @Transactional
    public CustomerDto createCustomer(CustomerDto dto) {
        BusinessProfile bp = securityUtil.getCurrentBusinessProfile();

        Customer c = new Customer();
        c.setBusiness(bp);
        c.setName(dto.getName().trim());
        c.setCompanyName(dto.getCompanyName());
        c.setPhone(dto.getPhone());
        c.setEmail(dto.getEmail());
        c.setAddress(dto.getAddress());
        c.setCity(dto.getCity());
        c.setState(dto.getState());
        c.setPincode(dto.getPincode());
        c.setNotes(dto.getNotes());

        Customer saved = customerRepository.save(c);
        return mapToDto(saved);
    }

    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto dto) {
        Customer c = getCustomerEntityById(id);

        c.setName(dto.getName().trim());
        c.setCompanyName(dto.getCompanyName());
        c.setPhone(dto.getPhone());
        c.setEmail(dto.getEmail());
        c.setAddress(dto.getAddress());
        c.setCity(dto.getCity());
        c.setState(dto.getState());
        c.setPincode(dto.getPincode());
        c.setNotes(dto.getNotes());

        Customer updated = customerRepository.save(c);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer c = getCustomerEntityById(id);
        customerRepository.delete(c);
    }

    public CustomerDto mapToDto(Customer c) {
        CustomerDto dto = new CustomerDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setCompanyName(c.getCompanyName());
        dto.setPhone(c.getPhone());
        dto.setEmail(c.getEmail());
        dto.setAddress(c.getAddress());
        dto.setCity(c.getCity());
        dto.setState(c.getState());
        dto.setPincode(c.getPincode());
        dto.setNotes(c.getNotes());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
