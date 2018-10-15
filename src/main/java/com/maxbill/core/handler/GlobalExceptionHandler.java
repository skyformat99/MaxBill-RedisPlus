package com.maxbill.core.handler;
//
//
//import org.apache.log4j.Logger;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletResponse;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    private static Logger log = Logger.getLogger(GlobalExceptionHandler.class);
//
//
//    public static final String DEFAULT_ERROR_VIEW = "error";
//
//    @ExceptionHandler(value = Exception.class)
//    public ModelAndView defaultErrorHandler(HttpServletResponse response, Exception e) {
//        ModelAndView mav = new ModelAndView();
//        log.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        log.error(e);
//        log.error("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//        mav.addObject("code", response.getStatus());
//        mav.setViewName(DEFAULT_ERROR_VIEW);
//        return mav;
//    }
//}
