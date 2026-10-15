
package com.example.ajay.repo;

import com.example.ajay.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByEmail(String email);

    boolean existsByEmail(String email);

    // Get all staff belonging to a specific admin
    List<Staff> findByAdmin_Id(Long adminId);

    // Find a staff member only if they belong to that admin
    Optional<Staff> findByIdAndAdmin_Id(Long id, Long adminId);
}
