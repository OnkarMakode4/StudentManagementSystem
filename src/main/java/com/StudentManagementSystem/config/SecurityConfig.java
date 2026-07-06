package com.StudentManagementSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Public
                .requestMatchers(
                    "/signup", "/login",
                    "/forgot-password", "/reset-password",
                    "/css/**", "/uploads/photos/**"
                ).permitAll()

                // ADMIN only
                .requestMatchers(
                    "/students/new",
                    "/students/edit/**",
                    "/courses/**",
                    "/attendance",
                    "/attendance/delete/**",
                    "/grades/delete/**",
                    "/announcements/delete/**",
                    "/timetable/delete/**",
                    "/fees/delete/**",
                    "/fees"
                ).hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST,
                    "/students/**",
                    "/grades/**",
                    "/announcements",
                    "/timetable",
                    "/fees/**"
                ).hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET,
                    "/students/{id:[0-9]+}"
                ).hasRole("ADMIN")

                // Any logged-in user
                .requestMatchers(HttpMethod.GET,
                    "/students",
                    "/students/view/**",
                    "/students/idcard/**",
                    "/students/export/**",
                    "/dashboard",
                    "/attendance/student/**",
                    "/grades/student/**",
                    "/announcements",
                    "/timetable",
                    "/fees/student/**"
                ).authenticated()

                .requestMatchers("/profile").authenticated()

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/students", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/login")
                .permitAll()
            );

        return http.build();
    }
}