package com.example.bookStore.demo.Dtos;


import com.example.bookStore.demo.Entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProfileRequest {


  @Email
  @NotNull
    private String email;

  @NotNull
    private String name;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;





    private Role role;
}
