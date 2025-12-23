package com.example.bookStore.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class User {

    @Id
    @GeneratedValue(strategy =GenerationType.AUTO)

    private Long id;
    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    private String resetOtp;
    private String verifyOtp;
    private LocalDateTime expire_verifyOtp_at;
    private LocalDateTime expire_resetOtp_at;
    private boolean isAccountVerified = false;
    private  String provider;

}
