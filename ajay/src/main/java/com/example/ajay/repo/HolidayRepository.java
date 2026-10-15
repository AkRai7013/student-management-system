
package com.example.ajay.repo;

import com.example.ajay.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByAdmin_IdOrderByHolidayDateAsc(Long adminId);

    Optional<Holiday> findByIdAndAdmin_Id(Long id, Long adminId);
}
