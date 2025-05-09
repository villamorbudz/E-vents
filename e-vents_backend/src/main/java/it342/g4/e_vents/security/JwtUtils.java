package it342.g4.e_vents.security;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import it342.g4.e_vents.config.JwtProperties;
import it342.g4.e_vents.model.User;

@Component
public class JwtUtils {
    
    private final JwtProperties jwtProperties;
    private final Key key;
    
    @Autowired
    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
    
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getUserId());
        // We still include the role info in the token, but it won't be used for authorization
        claims.put("role", user.getRole() != null ? user.getRole().getName() : "USER");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    /**
     * For simplified authentication, we return a standard list of authorities.
     * This ensures any authenticated user has the same level of access.
     */
    public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
        // Instead of checking roles, we just give every authenticated user a standard authority
        return new ArrayList<>(); // Empty authorities list - just being authenticated is enough
    }
}