package com.mdairy.repository;

import com.mdairy.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerCode(String customerCode);

    boolean existsByCustomerCode(String customerCode);

    boolean existsByCustomerCodeAndIdNot(String customerCode, Long id);

    List<Customer> findByActiveOrderByCustomerCodeAsc(Boolean active);

    List<Customer> findAllByOrderByCustomerCodeAsc();

    long countByActiveTrue();

    @Query("SELECT c FROM Customer c WHERE " +
           "(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:active IS NULL OR c.active = :active) " +
           "ORDER BY c.customerCode ASC")
    List<Customer> searchCustomers(@Param("search") String search, @Param("active") Boolean active);
}
