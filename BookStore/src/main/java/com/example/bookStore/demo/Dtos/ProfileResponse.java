package com.example.bookStore.demo.Dtos;


import com.example.bookStore.demo.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {


    private String name;
    private String email;
    private Role role ;
    private String token;
    private String message;
    private String verifyOtp;
}
