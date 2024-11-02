package com.example.demo.Config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.Service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deaktivace ochrany proti CROSS-SITE REQUEST FORGERY 
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.setAllowedOrigins(List.of("http://localhost:4200", "http://192.168.56.1:4200")); // Povolit požadavky z localhost:4200
                corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                //corsConfig.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
                corsConfig.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "Cookie", "Set-Cookie"));
                corsConfig.setAllowCredentials(true);
                corsConfig.setMaxAge(3600L); // Nastavení maximální doby platnosti CORS v sekundách
                return corsConfig;
            }))
            .authorizeHttpRequests(auth -> auth
                //.requestMatchers("/api/users/me").hasRole("Admin")
                //.requestMatchers("/api/users/me").authenticated()
                //.requestMatchers("/api/users/verify-password").authenticated()
                //.requestMatchers("/api/users/change-password").authenticated()
                //.requestMatchers("/api/users/verify-password").hasRole("Admin")
                //.requestMatchers("/api/users/change-password").hasRole("Admin")
                .anyRequest().permitAll() // Povolit přístup ke všem ostatním endpointům
            )
            .formLogin(form -> form.disable()) // Zakázat login form
            .logout(logout -> logout.permitAll())
             //.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Politika vytváření session
                .sessionFixation().none() // Vyhnout se session fixation útokům
                .invalidSessionUrl("/session-invalid") // URL pro přesměrování po neplatné session
                .maximumSessions(1) // Maximální počet session na uživatele
                .expiredUrl("/session-expired") // URL pro přesměrování po vypršení session
            )

            .headers(headers -> headers
                .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy", "")) // Prázdná hlavička
            );

            /* 
            .headers(headers -> headers
                .addHeaderWriter(new ContentSecurityPolicyHeaderWriter(
                 
                    //"default-src 'self'; script-src 'self' 'unsafe-inline'; object-src 'none'; frame-ancestors 'none';"
                      "script-src * 'unsafe-inline'; object-src 'none'; frame-ancestors 'none';"
                ))
            );
            */

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        //return new Md5PasswordEncoder();
    }
   /*     
    @Bean
    public Md5PasswordEncoder Md5passwordEncoder() {
        return new Md5PasswordEncoder();
    }
        */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


}
