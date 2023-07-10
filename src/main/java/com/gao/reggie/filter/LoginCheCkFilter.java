package com.gao.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.gao.reggie.common.BaseContext;
import com.gao.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义过滤器
 */
@WebFilter(filterName = "loginCheCkFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheCkFilter implements Filter {
    //匹配路径支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求{}", requestURI);

        //不处理直接放行
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
        };

        //本次URL需要处理否
        boolean check = check(urls, requestURI);

        //如果不需要处理，则直接放行
        if (check = true) {
            log.info("本次请求 {} 不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //如果登陆状态，如果已经登陆，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已经登陆，用户ID为{}", request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        //如果未登录，则返回登陆结果,通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配 查看当次需要放行否
     *
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
