package com.telyutalks.telyutalks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**")
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/images/**", "/service-worker.js", "/manifest.json", "/icons/**").permitAll()
                .requestMatchers("/admin/login  ").permitAll() 
                .anyRequest().hasRole("ADMIN")
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout")
            );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                .cacheControl(cache -> cache.disable())
            )
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/login", "/register").anonymous()
                .requestMatchers("/ask", "/profile", "/question/{id}/answers").hasAnyRole("STUDENT", "LECTURER")
                .requestMatchers("/question/{id}").permitAll()
                .requestMatchers(
                    "/", "/register/**", "/search", "/api/**", 
                    "/css/**", "/js/**", "/images/**", "/service-worker.js", "/manifest.json", "/icons/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("username") 
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
