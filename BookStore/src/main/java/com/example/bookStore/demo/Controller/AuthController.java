package com.example.bookStore.demo.Controller;


import com.example.bookStore.demo.Dtos.AuthRequest;
import com.example.bookStore.demo.Dtos.AuthResponse;
import com.example.bookStore.demo.Dtos.ProfileRequest;
import com.example.bookStore.demo.Dtos.ProfileResponse;
import com.example.bookStore.demo.Entity.User;
import com.example.bookStore.demo.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final UserService userService;


    @PostMapping("/register")

    public ResponseEntity<ProfileResponse> register(@RequestBody @Valid ProfileRequest request){
      ProfileResponse response =  userService.createUser(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }





    @PostMapping("/login")

    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest){
        AuthResponse response = userService.userLogin(authRequest);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<User> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }








}
