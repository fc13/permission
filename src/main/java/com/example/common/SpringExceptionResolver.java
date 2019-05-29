package com.example.common;

import com.example.exception.ParamException;
import com.example.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        String url = request.getRequestURL().toString();
        String errorMsg = "system error";
        ModelAndView mv;
        JsonData result;
        if (url.endsWith(".json")) { //项目中将所有返回json数据的接口的url定义为.json结尾
            if (e instanceof PermissionException || e instanceof ParamException) {
                result = JsonData.fail(e.getMessage());
            } else {
                log.error("unknown exception,url:" + url, e);
                result = JsonData.fail(errorMsg);
            }
            mv = new ModelAndView("jsonView", result.toMap());
        } else if (url.endsWith(".page")) { //项目中将所有返回页面的接口的url定义为.page结尾
            log.error("unknown exception,url:" + url, e);
            result = JsonData.fail(errorMsg);
            mv = new ModelAndView("exception", result.toMap());
        } else {
            log.error("unknown exception,url:" + url, e);
            result = JsonData.fail(errorMsg);
            mv = new ModelAndView("jsonView", result.toMap());
        }
        return mv;
    }
}
