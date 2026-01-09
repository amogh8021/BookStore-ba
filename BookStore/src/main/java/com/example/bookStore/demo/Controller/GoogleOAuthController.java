package com.example.bookStore.demo.Controller;

import com.example.bookStore.demo.Entity.User;
import com.example.bookStore.demo.Jwt.JwtService;
import com.example.bookStore.demo.Service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class GoogleOAuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @PostMapping("/oauth/google")
    public ResponseEntity<?> googleAuth(@RequestBody Map<String, String> body) throws Exception {

        String idTokenString = body.get("token");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);

        if (idToken == null) {
            throw new RuntimeException("Invalid Google ID Token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = userService.findOrCreateOAuthUser(email, name, "GOOGLE");

        String jwt = jwtService.generateToken((UserDetails) user);

        return ResponseEntity.ok(Map.of("jwt", jwt));
    }


}
