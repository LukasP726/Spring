package com.example.demo.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        //byte[] secretKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 864_000_000))
            .signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
            .compact();
    }
    


    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            // Ověření podpisu tokenu a získání nároků
            Claims claims = extractAllClaims(token);
    
            // Ověření, že uživatelské jméno v tokenu odpovídá uživateli a token není vypršelý
            String username = claims.getSubject();
        if (!username.equals(userDetails.getUsername())) {
            return false;
        }

        // Získání rolí z tokenu
        List<String> tokenRoles = claims.get("roles", List.class);
        List<String> userRoles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        // Ověření, že role z tokenu odpovídají rolím uživatele
        if (!tokenRoles.containsAll(userRoles) || userRoles.size() != tokenRoles.size()) {
            return false;
        }

        // Ověření, že token není vypršelý
        return !isTokenExpired(token);
        } catch (Exception e) {
            // Logování chyby pro ladění
            e.printStackTrace();
            return false; // Token není validní
        }
    }
    
}
