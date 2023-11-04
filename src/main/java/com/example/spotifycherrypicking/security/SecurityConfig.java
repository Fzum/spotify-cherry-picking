package com.example.spotifycherrypicking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    // todo refactor; can i call oauth2Login() on the HttpSecurity object directly?
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/initiateLogin").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oAuth2LoginConfigurer -> oAuth2LoginConfigurer
                        .defaultSuccessUrl("http://localhost:4200/home")
                        .failureUrl("http://localhost:4200/error"));

        return http.build();
    }
}
