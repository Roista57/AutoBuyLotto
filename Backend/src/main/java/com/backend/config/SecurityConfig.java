package com.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 보호 비활성화 (개발 환경에서만 사용 권장)
            .csrf(csrf -> csrf.disable())
            
            // 인증 설정
            .authorizeHttpRequests(auth -> auth
                // /members/** 경로는 인증 없이 접근 허용
                .requestMatchers("**").permitAll()
                
                // 그 외의 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // HTTP Basic 인증 활성화
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
