package com.mdairy.repository;

import com.mdairy.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Optional<Vendor> findByVendorCode(String vendorCode);

    boolean existsByVendorCode(String vendorCode);

    boolean existsByVendorCodeAndIdNot(String vendorCode, Long id);

    List<Vendor> findByActiveOrderByVendorCodeAsc(Boolean active);

    List<Vendor> findAllByOrderByVendorCodeAsc();

    long countByActiveTrue();

    @Query("SELECT v FROM Vendor v WHERE " +
           "(:search IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(v.vendorCode) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(v.phone) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:active IS NULL OR v.active = :active) " +
           "ORDER BY v.vendorCode ASC")
    List<Vendor> searchVendors(@Param("search") String search, @Param("active") Boolean active);
}
