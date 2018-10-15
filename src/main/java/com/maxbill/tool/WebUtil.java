package com.maxbill.tool;
//
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
//public class WebUtil {
//    /**
//     * 获取request
//     */
//    public static HttpServletRequest getRequest() {
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        return requestAttributes == null ? null : requestAttributes.getRequest();
//    }
//
//
//    /**
//     * 获取session
//     */
//    public static HttpSession getSession() {
//        return getRequest().getSession(false);
//    }
//
//    /**
//     * 获取真实路径
//     */
//    public static String getRealRootPath() {
//        return getRequest().getServletContext().getRealPath("/");
//    }
//
//
//    /**
//     * 获取session中的Attribute
//     */
//    public static Object getSessionAttribute(String key) {
//        HttpServletRequest request = getRequest();
//        return request == null ? null : request.getSession().getAttribute(key);
//    }
//
//    /**
//     * 设置session的Attribute
//     */
//    public static void setSessionAttribute(String key, Object value) {
//        HttpServletRequest request = getRequest();
//        if (request != null) {
//            request.getSession().setAttribute(key, value);
//        }
//    }
//
//    /**
//     * 获取request中的Attribute
//     */
//    public static Object getRequestAttribute(String key) {
//        HttpServletRequest request = getRequest();
//        return request == null ? null : request.getAttribute(key);
//    }
//
//    /**
//     * 设置request的Attribute
//     */
//    public static void setRequestAttribute(String key, Object value) {
//        HttpServletRequest request = getRequest();
//        if (request != null) {
//            request.setAttribute(key, value);
//        }
//    }
//
//    /**
//     * 获取上下文path
//     */
//    public static String getContextPath() {
//        return getRequest().getContextPath();
//    }
//
//    /**
//     * 删除session中的Attribute
//     */
//    public static void removeSessionAttribute(String key) {
//        getRequest().getSession().removeAttribute(key);
//    }
//
//}
