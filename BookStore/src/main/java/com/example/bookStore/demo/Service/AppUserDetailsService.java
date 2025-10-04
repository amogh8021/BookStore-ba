package com.example.bookStore.demo.Service;

import com.example.bookStore.demo.Entity.User;
import com.example.bookStore.demo.Repository.UserRepository;
import com.example.bookStore.demo.Security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AppUserDetailsService implements UserDetailsService {
    private  final UserRepository userRepository;
    @Override

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        var existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("email not found for the user  " + email));

        return new CustomUserDetails(existingUser);
    }
}
