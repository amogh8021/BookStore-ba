package com.example.bookStore.demo.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;



    public String generateToken(UserDetails userDetails){
        Map<String, Object>extraClaims = new HashMap<>();

        //convert authorities into a list of a role name

        List<String>roles = userDetails.getAuthorities()
                        .stream()
                                .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.toList());
        extraClaims.put("roles",roles);
        return createToken(extraClaims,userDetails);
    }

    private String createToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +  1000 * 60 * 60 * 10))
                .signWith(getSignInKey())
                .compact();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    //for jwt filter
    //generic method

    public <T> T extractClaim(String token , Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    //extract username

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    //extract expiry Date

    public Date extractExpiryDate(String token){

        return extractClaim(token,Claims::getExpiration);

    }

    //extract user roles or authorities

    public List<String> extractRoles(String token){
      Claims claims  =extractAllClaims(token);
      return claims.get("roles", List.class);
    }

    //is Token Expired

    public Boolean isTokenExpired (String token){
        return extractExpiryDate(token).before(new Date());
    }


    //is Token valid

    public Boolean isTokenValid(String token , UserDetails userDetails){
        final String username = extractUsername(token);
        return (!isTokenExpired(token) &&  username.equals(userDetails.getUsername()) );
    }


}
