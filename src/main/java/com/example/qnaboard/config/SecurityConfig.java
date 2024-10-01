package com.example.qnaboard.config;

import com.example.qnaboard.oauth2.CustomClientRegistrationRepo;
import com.example.qnaboard.oauth2.CustomOAuth2AuthorizedClientService;
import com.example.qnaboard.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService;
    private final JdbcTemplate jdbcTemplate;


    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomClientRegistrationRepo customClientRegistrationRepo, CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService, JdbcTemplate jdbcTemplate){
        this.customOAuth2UserService = customOAuth2UserService;
        this.customClientRegistrationRepo = customClientRegistrationRepo;
        this.customOAuth2AuthorizedClientService = customOAuth2AuthorizedClientService;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .addFilterBefore(corsFilter(), SecurityContextPersistenceFilter.class);
        http
                .authorizeHttpRequests((auth) -> auth
                        // 작동순서: 위에서 아래로 -> 즉, 아래에서 모든 권한을 다룰 수 있도록 하자.
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/oauth2/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/user/login/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/loginProc")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/user/join")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/question/list")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/my/**")).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(new AntPathRequestMatcher("/question/**")).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll() // H2 콘솔 접근 허용
                        .anyRequest().authenticated());
        http
                .formLogin((login) -> login.loginPage("/user/login")
                        .defaultSuccessUrl("/main") // 로그인 성공시 이동할 페이지
                        .failureHandler((request, response, exception) -> {
                            // 로그인 실패 시 로그 출력
                            System.err.println("Login failed: " + exception.getMessage());
                            response.sendRedirect("/user/login?error");
                        })
                        .permitAll());
        http
                .logout((logout) -> logout.logoutUrl("/user/logout")
                        .logoutSuccessUrl("/main")
                        .invalidateHttpSession(true)
                );
        http
                .httpBasic((basic) -> basic.disable());
        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/user/login")
                        .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                        .authorizedClientService(customOAuth2AuthorizedClientService.oAuth2AuthorizedClientService(jdbcTemplate, customClientRegistrationRepo.clientRegistrationRepository()))
                        .userInfoEndpoint((userInfoEndpointConfig) ->
                                userInfoEndpointConfig.userService(customOAuth2UserService)));
        http
                .csrf((csrf) -> csrf.disable());
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))) // H2 콘솔 사용 시 CSRF 비활성화
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // H2 콘솔 프레임 허용

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000"); // 리액트
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
