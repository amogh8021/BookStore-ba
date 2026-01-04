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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    // ================= Create user (FINAL SIGNUP) =================
    public ProfileResponse createUser(ProfileRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();

        Optional<User> existingUserOpt = userRepository.findByEmail(cleanEmail);

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // 🔥 FIX: if password already exists → user already registered
            if (existingUser.getPassword() != null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Email already registered"
                );
            }

            // COMPLETE SIGNUP AFTER OTP VERIFICATION
            existingUser.setName(request.getName());
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            existingUser.setRole(request.getRole() != null ? request.getRole() : Role.USER);
            existingUser.setAccountVerified(true);

            User updatedUser = userRepository.save(existingUser);
            return convertToProfileResponse(updatedUser);
        }

        // EDGE CASE: direct signup without OTP
        Role role = request.getRole() != null ? request.getRole() : Role.USER;
        User newUser = User.builder()
                .name(request.getName())
                .email(cleanEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isAccountVerified(true)
                .build();

        User savedUser = userRepository.save(newUser);
        return convertToProfileResponse(savedUser);
    }

    private ProfileResponse convertToProfileResponse(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwt = jwtService.generateToken(userDetails);

        return ProfileResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .message("User registered successfully")
                .token(jwt)
                .build();
    }

    // ================= Login =================
    public AuthResponse userLogin(AuthRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Wrong password");

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwt = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .token(jwt)
                .message("Login successful")
                .build();
    }

    // ================= Send OTP =================
    public String sendOtpOnSignup(String email) {
        String cleanEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(cleanEmail).orElse(null);

        // If user exists AND already completed signup
        if (user != null && user.getPassword() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already registered"
            );
        }

        // If user does not exist → create email-only user
        if (user == null) {
            user = User.builder()
                    .email(cleanEmail)
                    .role(Role.USER)
                    .isAccountVerified(false)
                    .build();
        }

        // Generate OTP
        String otp = String.valueOf(100000 + (int) (Math.random() * 900000));
        user.setVerifyOtp(otp);
        user.setExpire_verifyOtp_at(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);
        emailService.SendOtpOnSignup(cleanEmail, otp);

        return "OTP sent successfully";
    }

    // ================= Verify OTP =================
    public String verifySignupOtp(String email, String otp) {
        String cleanEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getVerifyOtp() == null)
            throw new RuntimeException("OTP already verified or not sent");

        if (!user.getVerifyOtp().equals(otp))
            throw new RuntimeException("Invalid OTP");

        if (LocalDateTime.now().isAfter(user.getExpire_verifyOtp_at()))
            throw new RuntimeException("OTP expired");

        // 🔥 FIX: DO NOT mark account verified here
        user.setVerifyOtp(null);
        user.setExpire_verifyOtp_at(null);

        userRepository.save(user);
        return "OTP verified successfully";
    }

    // ================= Utilities =================
    public User getUserFromPrincipal(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return userRepository.findAll(pageable);
    }

    // ================= Forgot Password =================
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Email not registered"
                ));

        String otp = String.valueOf(100000 + (int) (Math.random() * 900000));
        user.setResetOtp(otp);
        userRepository.save(user);

        emailService.sendForgotPassword(email, otp);
        return "OTP sent to email";
    }

    public String verifyResetOtp(String email, String otp) {
        String cleanEmail = email.trim().toLowerCase();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Email not registered"
                ));

        if (user.getResetOtp() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not requested");

        if (!user.getResetOtp().equals(otp))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");

        user.setResetOtp(null);
        userRepository.save(user);

        return "OTP verified successfully";
    }

    public String resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found for this email"
                ));

        if (user.getResetOtp() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please verify OTP before resetting password"
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password updated successfully";
    }

    // ================= OAuth =================
    public User findOrCreateOAuthUser(String email, String name, String provider) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setProvider(provider);
                    newUser.setRole(Role.USER);
                    newUser.setAccountVerified(true);
                    return userRepository.save(newUser);
                });
    }

    public ProfileResponse getMyProfile(Principal principal) {
        User user = getUserFromPrincipal(principal);

        return ProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public ProfileResponse updateProfile(ProfileRequest request, Principal principal) {
        User user = getUserFromPrincipal(principal);

        user.setName(request.getName());
        userRepository.save(user);

        return ProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Profile updated successfully")
                .build();
    }

}
