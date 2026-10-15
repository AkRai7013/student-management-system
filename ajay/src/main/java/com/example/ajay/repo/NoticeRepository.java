
package com.example.ajay.repo;

import com.example.ajay.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByAdmin_IdOrderByNoticeDateDesc(Long adminId);

    Optional<Notice> findByIdAndAdmin_Id(Long id, Long adminId);
}
