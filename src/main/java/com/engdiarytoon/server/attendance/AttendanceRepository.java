package com.engdiarytoon.server.attendance;

import com.engdiarytoon.server.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Find attendance records for a specific user
    List<Attendance> findByUser(User user);

    // Find attendance by user and date (to check if attendance exists for today)
    Optional<Attendance> findByUserAndDate(User user, LocalDate date);
}