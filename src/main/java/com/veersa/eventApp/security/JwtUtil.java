package com.veersa.eventApp.security;


import com.veersa.eventApp.exception.JwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    private Claims extractAllClaims(String token) {
        try{
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        }
        catch (ExpiredJwtException ex) {
            throw new JwtException("Token has expired", ex);
        } catch (UnsupportedJwtException ex) {
            throw new JwtException("Unsupported JWT token", ex);
        } catch (MalformedJwtException ex) {
            throw new JwtException("Invalid or malformed JWT", ex);
        } catch (SignatureException ex) {
            throw new JwtException("Invalid JWT signature", ex);
        } catch (IllegalArgumentException ex) {
            throw new JwtException("JWT claims string is empty", ex);
        }catch (Exception ex) {
            throw new JwtException("An error occurred while parsing the JWT", ex);
        }
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Check if the username in the token matches the userDetails and if the token is not expired
        if(!username.equals(userDetails.getUsername())) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
        if(isTokenExpired(token)) {
            throw new JwtException("Token is expired");
        }
        return true;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

}

