package com.engdiarytoon.server.happiness;

import com.engdiarytoon.server.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "happiness")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Happiness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "happy_id", nullable = false, updatable = false)
    private Long happyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "value", nullable = false)
    private float value;
}