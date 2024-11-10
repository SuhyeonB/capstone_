package com.engdiarytoon.server.happiness;

import com.engdiarytoon.server.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class HappinessServiceImpl implements HappinessService {

    private final HappinessRepository happinessRepository;

    @Autowired
    public HappinessServiceImpl(HappinessRepository happinessRepository) {
        this.happinessRepository = happinessRepository;
    }

    @Override
    @Transactional
    public Happiness recordHappiness(User user, float value) {
        LocalDate today = LocalDate.now();

        // Check if today's happiness is already recorded
        if (happinessRepository.findByUserAndDate(user, today).isPresent()) {
            throw new IllegalStateException("Happiness already recorded for today.");
        }

        // Create a new happiness entry
        Happiness happiness = Happiness.builder()
                .user(user)
                .date(today)
                .value(value)
                .build();

        return happinessRepository.save(happiness);
    }

    @Override
    @Transactional
    public Happiness updateHappiness(User user, float value) {
        LocalDate today = LocalDate.now();
        Happiness happiness = happinessRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new IllegalArgumentException("No happiness record found for today to update."));

        happiness.setValue(value);
        return happinessRepository.save(happiness);
    }

    @Override
    public Optional<Happiness> getTodayHappiness(User user) {
        LocalDate today = LocalDate.now();
        return happinessRepository.findByUserAndDate(user, today);
    }

    @Override
    @Transactional
    public void deleteTodayHappiness(User user) {
        LocalDate today = LocalDate.now();
        Happiness happiness = happinessRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new IllegalArgumentException("No happiness record found for today to delete."));

        happinessRepository.delete(happiness);
    }
}
