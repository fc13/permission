package com.example.filter;

import com.example.common.ApplicationContextHelper;
import com.example.common.JsonData;
import com.example.common.RequestHolder;
import com.example.model.SysUser;
import com.example.service.SysCoreService;
import com.example.util.JsonMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class AclControllerFilter implements Filter {

    private static Set<String> exclusionUrlSet = Sets.newConcurrentHashSet();
    private final static String noAuth = "/sys/user/noAuth.page";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String exclusionUrl = filterConfig.getInitParameter("exclusionUrl");
        List<String> exclusionUrlList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(exclusionUrl);
        exclusionUrlSet = Sets.newConcurrentHashSet(exclusionUrlList);
        exclusionUrlSet.add(noAuth);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Map parameterMap = request.getParameterMap();
        String path = request.getServletPath();
        String requestUrl = request.getRequestURL().toString();
        log.info("servletPath = {},requestUrl = {}", path, requestUrl);
        if (exclusionUrlSet.contains(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        SysUser user = RequestHolder.getCurrentUser();
        if (user == null) {
            log.info("有人访问url：{}，但是没有登录，参数为：{}", path, JsonMapper.objectToString(parameterMap));
            noAuth(request, response);
            return;
        }
        SysCoreService coreService = ApplicationContextHelper.popBean(SysCoreService.class);
        if (coreService != null && !coreService.hasUrlAcl(path)) {
            log.info("{}访问url：{}，但是没有，参数为：{}", JsonMapper.objectToString(user), path, JsonMapper.objectToString(parameterMap));
            noAuth(request,response);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }

    private void noAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getServletPath();
        if (path.endsWith(".json")){
            JsonData jsonData = JsonData.fail("没有访问权限，如若需要访问，请联系管理员");
            response.setHeader("Content-Type","application/json");
            response.getWriter().print(JsonMapper.objectToString(jsonData));
        }else {
            response.setHeader("Content-Type", "text/html");
            response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                    + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                    + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                    + "window.location.href='" + path + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
        }
    }

    @Override
    public void destroy() {

    }
}
