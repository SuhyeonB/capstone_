package com.engdiarytoon.server.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto inc
    @Column(name="user_id", nullable = false, updatable = false)
    private Long userId;
    private String name;
    private String email;
    private String password;

    @Column(name="delete_flag")
    private boolean deleteFlag;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    private String oauthProvider;
    //private String oauthId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    protected void onCreate() { //default
        this.createdAt = LocalDateTime.now();
        this.deleteFlag = false;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}