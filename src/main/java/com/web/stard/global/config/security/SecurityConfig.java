package com.web.stard.global.config.security;

import com.web.stard.global.utils.HeaderUtils;
import com.web.stard.global.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final HeaderUtils headerUtils;
    private final RedisUtils redisUtils;

    private static final String[] PERMIT_ALL_PATTERNS = new String[] {
            "/members/auth/join", "/members/auth/check-email", "/members/auth/check-nickname",
            "/members/auth/join/additional-info", "/members/auth/sign-in", "/members/auth/reissue",
            "/members/auth/auth-codes", "/members/auth/auth-codes/verify",
            "/studies/search", "/members/auth/find-password", "/members/reset-password",
            "/members/auth/valid-password-reset-token", "/gs-guide-websocket"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMIT_ALL_PATTERNS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/communities/**").permitAll() // 커뮤니티 - 조회는 인증 없이 가능
                        .requestMatchers(HttpMethod.GET, "/notices/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/faqs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/faqs-and-qnas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/qnas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/replies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/studies/{studyId}").permitAll()
                        .anyRequest().authenticated() // 다른 모든 요청은 인증 필요
                )
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);     // 세션 생성 X
                })
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, headerUtils, redisUtils),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("https://star-d-frontend.vercel.app");
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**");
    }

}
