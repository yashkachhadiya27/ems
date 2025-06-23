package com.backend.ems.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.backend.ems.Filter.JWTAuthFilter;
import com.backend.ems.Util.JWTAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class SecurityConfig {
        private final JWTAuthFilter jwtAuthFilter;
        private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                httpSecurity.csrf(AbstractHttpConfigurer::disable)
                                .cors(Customizer.withDefaults())
                                .authorizeHttpRequests(request -> request
                                                .requestMatchers("/public/**", "/v2/api-docs",
                                                                "/v3/api-docs",
                                                                "/v3/api-docs/**", "/swagger-resources",
                                                                "/swagger-resources/**", "/configuration/ui",
                                                                "/configuration/security", "/swagger-ui/**",
                                                                "/webjars/**", "/swagger-ui.html", "/ws/**", "/api/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
                                                .requestMatchers("/employee/**").hasAnyAuthority("USER")
                                                .requestMatchers("/adminEmployee/**").hasAnyAuthority("ADMIN", "USER"))

                                .sessionManagement(manager -> manager
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .oauth2Login(oauth2Login -> oauth2Login
                                                .loginPage("/public/oauth2/authorization/google"))
                                .addFilterBefore(
                                                jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
                httpSecurity.exceptionHandling(exception -> exception
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint));

                return httpSecurity.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

}
