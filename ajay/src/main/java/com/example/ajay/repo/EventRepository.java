
package com.example.ajay.repo;

import com.example.ajay.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByAdmin_IdOrderByEventDateAsc(Long adminId);

    Optional<Event> findByIdAndAdmin_Id(Long id, Long adminId);
}
