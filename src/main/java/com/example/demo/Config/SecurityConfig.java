package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.StaticHeadersWriter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Konfigurační metoda pro nastavení bezpečnostního filtru aplikace pomocí Spring Security.
     * Vrací řetězec filtrů (SecurityFilterChain), který definuje pravidla pro zabezpečení HTTP požadavků.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deaktivace ochrany proti CROSS-SITE REQUEST FORGERY 
            .cors(cors -> cors.disable())
             /* 
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200", "http://192.168.56.1:4200")); // Povolit požadavky z localhost:4200
                corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfig.setAllowedHeaders(List.of("*"));
                corsConfig.setAllowCredentials(true);
                corsConfig.setMaxAge(3600L); // Nastavení maximální doby platnosti CORS v sekundách
                return corsConfig;
            }))
                */
                 
            .authorizeHttpRequests(auth -> auth
               
                .anyRequest().permitAll() // Povolit přístup ke všem ostatním endpointům
            )
            .formLogin(form -> form.disable()) // Zakázat login form
            .logout(logout -> logout.permitAll())
            
            .sessionManagement(session -> session
             
                .sessionFixation().none() // Vyhnout se session fixation útokům
              
                .maximumSessions(1) // Maximální počet session na uživatele
               
            )

            .headers(headers -> headers
                .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy", "")) // Prázdná hlavička
                .xssProtection(xss -> xss.disable()) // Vypne XSS ochranu
                //.frameOptions().sameOrigin()
                );

           
        return http.build();
    }
    
    /**
     * Definuje šifrovací algoritmus pro hesla.
     * V tomto případě je použit zastaralý MD5 – pro produkci nedoporučeno.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Md5PasswordEncoder();
    }
 
    /**
     * Získání instance správce autentizace.
     * Používá konfiguraci z ApplicationContextu.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


}
