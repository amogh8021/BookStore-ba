package com.example.bookStore.demo.Controller;


import com.example.bookStore.demo.Dtos.AuthRequest;
import com.example.bookStore.demo.Dtos.AuthResponse;
import com.example.bookStore.demo.Dtos.ProfileRequest;
import com.example.bookStore.demo.Dtos.ProfileResponse;
import com.example.bookStore.demo.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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





}
