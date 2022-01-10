package com.ricardo.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.ricardo.reggie.common.BaseContext;
import com.ricardo.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        //获取uri
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        //确定要放行的资源
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
//                "/common/**"
        };

        //判断路径是否需要放行
        boolean check = check(urls, requestURI);
        if (check){
            //放行
            log.info("路径直接放行：{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //判断员工是否已登录
        if (request.getSession().getAttribute("employee")!=null){
            log.info("员工已登录，员工ID为：{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        //判断用户是否已登录
        if (request.getSession().getAttribute("user")!=null){
            log.info("用户已登录，用户ID为：{}",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        //如果未登录，向用户发送消息
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }


    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean flag = PATH_MATCHER.match(url, requestURI);
            if (flag){
                return true;
            }
        }
        return false;
    }
}
