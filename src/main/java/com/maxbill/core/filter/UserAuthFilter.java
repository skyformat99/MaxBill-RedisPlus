package com.maxbill.core.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "userAuthFilter", urlPatterns = {"/*"})
public class UserAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = request.getRequestURI();
        if (url.indexOf("error") > -1) {
            filterChain.doFilter(request, response);
            return;
        }
        String ua = request.getHeader("User-Agent");
        if ("RedisPlus WebEngine".equals(ua)) {
            filterChain.doFilter(request, response);
            return;
        } else {
            response.setStatus(302);
            request.getRequestDispatcher("/error").forward(request, response);
        }
    }

    @Override
    public void destroy() {
    }

}
