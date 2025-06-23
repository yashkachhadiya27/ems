package com.backend.ems.Filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.backend.ems.Service.UserDetailServiceImpl;
import com.backend.ems.Util.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final UserDetailServiceImpl userDetailServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;
        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        if (authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);

            try {
                userEmail = jwtUtils.extractUsername(jwtToken);
                if (!jwtUtils.isTokenExpired(jwtToken)) {

                    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(userEmail);
                        if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(token);
                        }
                    }
                    filterChain.doFilter(request, response);

                }
            } catch (Exception e) {
                response.setStatus(498);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("message", e.getMessage());
                responseData.put("status", 498);

                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(response.getOutputStream(), responseData);
            }
        }

    }

}
