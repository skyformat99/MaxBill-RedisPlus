package com.maxbill.core.handler;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static final String DEFAULT_ERROR_VIEW = "error";


    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("msgs", System.err.toString());
        mav.addObject("code", response.getStatus());
        mav.setViewName(DEFAULT_ERROR_VIEW);
        return mav;
    }
}
