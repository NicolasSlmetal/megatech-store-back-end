package com.megatech.store.configuration;

import com.megatech.store.exceptions.ErrorType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class DeniedErrorHandler implements AccessDeniedHandler {

    private static final String templateErrorString = "{" +
            "\"message\":\"%s\",%n" +
            "\"code\":\"%s\"" +
            "}";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        String message = String.format(templateErrorString, accessDeniedException.getLocalizedMessage(), ErrorType.ACCESS_TOKEN_EXPIRED_OR_DENIED);
        response.getWriter().write(message);
    }
}
