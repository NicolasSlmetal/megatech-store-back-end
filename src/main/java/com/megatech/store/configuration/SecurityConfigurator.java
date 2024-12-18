package com.megatech.store.configuration;

import com.megatech.store.domain.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfigurator {

    private final AuthenticationFilter authenticationFilter;
    private final DeniedErrorHandler deniedErrorHandler;
    private final UnauthorizedEntryPoint unauthorizedEntryPoint;

    public SecurityConfigurator(AuthenticationFilter authenticationFilter, DeniedErrorHandler deniedErrorHandler, UnauthorizedEntryPoint unauthorizedEntryPoint) {
        this.authenticationFilter = authenticationFilter;
        this.deniedErrorHandler = deniedErrorHandler;
        this.unauthorizedEntryPoint = unauthorizedEntryPoint;
    }

    private static final String PRODUCT_ENDPOINT = "/products";
    private static final String CUSTOMER_ENDPOINT = "/customers";
    private static final String PURCHASE_ENDPOINT = "/purchases";
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling( handler ->
                        handler
                                .accessDeniedHandler(deniedErrorHandler)
                                .authenticationEntryPoint(unauthorizedEntryPoint))
                .cors(cors -> cors.configure(http))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                    .requestMatchers("/login").permitAll()
                    .requestMatchers(HttpMethod.GET, PRODUCT_ENDPOINT +"/stock/**").hasAuthority(Role.ADMIN.toString())
                    .requestMatchers(HttpMethod.GET, PRODUCT_ENDPOINT + "/**").permitAll()
                    .requestMatchers(HttpMethod.POST, PRODUCT_ENDPOINT).hasAuthority(Role.ADMIN.toString())
                    .requestMatchers(HttpMethod.PUT, PRODUCT_ENDPOINT + "/**").hasAuthority(Role.ADMIN.toString())
                    .requestMatchers(HttpMethod.DELETE, PRODUCT_ENDPOINT + "/**").hasAuthority(Role.ADMIN.toString())
                    .requestMatchers(CUSTOMER_ENDPOINT + "/me/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, CUSTOMER_ENDPOINT + "/**").hasAuthority(Role.ADMIN.toString())
                    .requestMatchers(HttpMethod.GET, CUSTOMER_ENDPOINT + "/**").hasAuthority(Role.ADMIN.toString())
                    .requestMatchers(HttpMethod.POST, CUSTOMER_ENDPOINT).permitAll()
                    .requestMatchers(HttpMethod.PUT, CUSTOMER_ENDPOINT + "/**").hasAuthority(Role.ADMIN.toString())
                    .requestMatchers(PURCHASE_ENDPOINT + "/total-value").hasAuthority(Role.ADMIN.toString())
                    .requestMatchers(PURCHASE_ENDPOINT + "/**").authenticated()

                ).addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder getDefaultPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
