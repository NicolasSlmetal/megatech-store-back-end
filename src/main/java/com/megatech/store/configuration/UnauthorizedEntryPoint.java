package com.megatech.store.configuration;

import com.megatech.store.exceptions.ErrorType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    private static final String templateErrorString = "{" +
            "\"message\":\"%s\",%n" +
            "\"code\":\"%s\"" +
            "}";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
       response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
       response.setContentType("application/json");
       String message = String.format(templateErrorString, "Need authentication before", ErrorType.ACCESS_TOKEN_EXPIRED_OR_DENIED);
       response.getWriter().write(message);
    }
}
