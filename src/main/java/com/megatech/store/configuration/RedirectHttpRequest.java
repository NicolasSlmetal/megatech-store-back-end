package com.megatech.store.configuration;

import jakarta.servlet.http.*;


public class RedirectHttpRequest extends HttpServletRequestWrapper {

    private String redirectUrl;

    public RedirectHttpRequest(HttpServletRequest request, String redirectUrl) {
        super(request);
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String getRequestURI() {
        return redirectUrl;
    }
}
