package com.engdiarytoon.server.happiness;

import com.engdiarytoon.server.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HappinessRepository extends JpaRepository<Happiness, Long> {

    // Find today's happiness record for a specific user
    Optional<Happiness> findByUserAndDate(User user, LocalDate date);
}
