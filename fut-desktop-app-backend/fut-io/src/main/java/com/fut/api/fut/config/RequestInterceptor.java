package com.fut.api.fut.config;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Intercept requests & responses before return.
 */
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Make fut-da a const somewhere, maybe in property.
        String futDa = request.getHeader("fut-da");

        if (futDa != null) {
            response.addHeader("fut-da", futDa);
        } else {
            // throw exception cause it ain't allowed.
//            System.out.println("not allowed");
        }
        return true; // throw exception if the header isn't present
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
