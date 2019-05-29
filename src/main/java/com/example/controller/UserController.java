package com.example.controller;

import com.example.dao.SysUserMapper;
import com.example.model.SysUser;
import com.example.service.SysUserService;
import com.example.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class UserController {

    @Resource
    private SysUserService service;

    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        SysUser sysUser = service.findByKeyword(username);
        String error = "";
        String ret = request.getParameter("ret");
        if (StringUtils.isBlank(username)){
            error = "用户名不能为空";
        }else if (StringUtils.isBlank(password)){
            error = "密码不能为空";
        }else if (sysUser == null){
            error = "未找到该用户";
        }else if (!sysUser.getPassword().equals(MD5Util.encrypt(password))){
            error = "用户名与密码不匹配";
        }else if (sysUser.getStatus()!=1){
            error = "用户处于冻结状态，请联系管理员";
        }else {
            request.getSession().setAttribute("user",sysUser);
            if (StringUtils.isBlank(ret)){
                response.sendRedirect("/admin/index.page");
                return;
            }else {
                response.sendRedirect(ret);
                return;
            }
        }
        request.setAttribute("error",error);
        request.setAttribute("username",username);
        if (StringUtils.isNotBlank(ret)){
            request.setAttribute("ret",ret);
        }
        String path = "signin.jsp";
        request.getRequestDispatcher(path).forward(request,response);
    }

    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        String path = "signin.jsp";
        response.sendRedirect(path);
    }
}
