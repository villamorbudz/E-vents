package it342.g4.e_vents.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    
    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extract token from Authorization header
            String header = request.getHeader("Authorization");
            
            // Debug logging
            System.out.println("JWT Filter - Request path: " + request.getRequestURI());
            System.out.println("JWT Filter - Auth header: " + (header != null ? header.substring(0, Math.min(20, header.length())) + "..." : "null"));
            
            if (header == null || !header.startsWith("Bearer ")) {
                System.out.println("No Bearer token found in request, continuing filter chain");
                filterChain.doFilter(request, response);
                return;
            }
            
            // Extract actual token
            String token = header.substring(7);
            
            // Validate token
            if (!jwtUtils.validateToken(token)) {
                System.out.println("Invalid token, continuing filter chain without authentication");
                filterChain.doFilter(request, response);
                return;
            }
            
            // Extract user details
            String email = jwtUtils.getEmailFromToken(token);
            List<SimpleGrantedAuthority> authorities = jwtUtils.getAuthoritiesFromToken(token);
            
            System.out.println("JWT Filter - Valid token for user: " + email);
            System.out.println("JWT Filter - Authorities: " + authorities);
            
            // Create authentication object
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(email, null, authorities);
            
            // Set authentication in Spring Security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("JWT Filter - Authentication set in security context");
        } catch (Exception e) {
            System.out.println("JWT Filter - Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}