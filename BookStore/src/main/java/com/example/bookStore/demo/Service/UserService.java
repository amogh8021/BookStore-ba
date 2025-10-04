package com.example.bookStore.demo.Service;


import com.example.bookStore.demo.Dtos.AuthRequest;
import com.example.bookStore.demo.Dtos.AuthResponse;
import com.example.bookStore.demo.Dtos.ProfileRequest;
import com.example.bookStore.demo.Dtos.ProfileResponse;
import com.example.bookStore.demo.Entity.Role;
import com.example.bookStore.demo.Entity.User;
import com.example.bookStore.demo.Jwt.JwtService;
import com.example.bookStore.demo.Repository.UserRepository;
import com.example.bookStore.demo.Security.CustomUserDetails;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // save the user to database

    public ProfileResponse  createUser(ProfileRequest request){

        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            System.out.println("email is found in the db ");
            throw new ResponseStatusException(HttpStatus.CONFLICT,"email already registered, please register with a different email");
        }

        //bt default it will be user role

        Role userRole = request.getRole() !=null ? request.getRole():Role.USER;

        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        User savedUser = userRepository.save(newUser);
        return convertToProfileResponse(savedUser);
    }

    private ProfileResponse convertToProfileResponse(User user) {

        UserDetails userDetails = new CustomUserDetails(user); // make sure this is used
        String jwt = jwtService.generateToken(userDetails);    // this will now work

        return ProfileResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .message("user is successfully registered")
                .token(jwt)
                .build();
    }


  //logic to login



    public AuthResponse userLogin(AuthRequest request ){

        //1.check whether user email exists or not in db and we will check it by using userRepository

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("user not found for this " + request.getEmail()));



        if(passwordEncoder.matches(request.getPassword(),user.getPassword())){

            CustomUserDetails userDetails = new CustomUserDetails(user);

            String jwt = jwtService.generateToken(userDetails);


            return AuthResponse.builder()
                    .message("you are successfully logged in ")
                    .token(jwt)
                    .role(user.getRole())
                    .email(request.getEmail())
                    .name(user.getName())
                    .build();


        }

        throw new BadCredentialsException("wrong password for this email");



    }

}