package com.megatech.store.configuration;

import com.megatech.store.exceptions.TokenErrorException;
import com.megatech.store.security.UserAuthentication;
import com.megatech.store.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {


    private final TokenService tokenService;
    public AuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = retrieveToken(request);

        if (token != null) {
            try {
                UserAuthentication user = new UserAuthentication(tokenService.verifyToken(token));
                Authentication authentication = getAuthenticationContext(user);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                if (request.getServletPath().endsWith("/id")) {
                    HttpServletRequestWrapper redirectHttpRequest = includeUserId(request, user);
                    filterChain.doFilter(redirectHttpRequest, response);
                    return;
                }

            } catch (TokenErrorException tokenErrorException) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static HttpServletRequestWrapper includeUserId(HttpServletRequest request, UserAuthentication user) {
        if (request.getServletPath().startsWith("/customers") || request.getServletPath().startsWith("/purchases")) {
            String uri = request.getRequestURI();
            String newUri = uri.replace("id", user.getUser().getId().toString());
            return new RedirectHttpRequest(request, newUri);
        }
        return new HttpServletRequestWrapper(request);
    }

    private static UsernamePasswordAuthenticationToken getAuthenticationContext(UserDetails user) {
        return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    public String retrieveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return null;
        }
        token = token.substring(7);
        return token;
    }
}
