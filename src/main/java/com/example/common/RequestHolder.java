package com.example.common;

import com.example.model.SysUser;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public class RequestHolder {
    private static ThreadLocal<SysUser> userHolder = new ThreadLocal<>();
    private static ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    public static void add(SysUser user){
        userHolder.set(user);
    }

    public static void add(HttpServletRequest request){
        requestHolder.set(request);
    }

    public static SysUser getCurrentUser(){
        return userHolder.get();
    }

    public static HttpServletRequest getCurrentRequest(){
        return requestHolder.get();
    }

    public static void remove(){
        userHolder.remove();
        requestHolder.remove();
    }
}
