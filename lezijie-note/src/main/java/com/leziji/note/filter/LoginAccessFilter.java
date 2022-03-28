package com.leziji.note.filter;

import com.leziji.note.po.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 非法访问拦截
 *  拦截资源
 *      所有的资源
 *
 *   需要被放行的资源
 *      1.指定页面,放行(用户无需登录的即可访问的资源:列如:登录页面login.jsp  注册页面register.jsp
 *      2.静态资源,放行(存放在statics目录下的资源:列如js,css,images等)
 *      3.指定行为,放行(用户无需登录即可执行的操作:列如:登录操作actionName=login等)
 *      4.登录状态,放行(判断session作用域中是否存在user对象:存在则放行,不存在,则拦截跳转到登录页面)
 *
 *   免登录（自动登录）
 *      通过Cookie对象实现
 *      什么时候使用免登录：
 *          当用户处于未登录状态，且取请求需要登录才能访问的资源时，调用自动登录功能
 *       目的：
 *          让用户处于登录状态（自动调用登录方法）
 *       实现：
 *          从Cookie对象中获取用户的姓名与密码，自动执行登录操作
 *              1.获取Cookie数组 request.getCookies();
 *              2.判断Cookie数组
 *              3.便利Cookie数组，获取指定的Cookie对象（name为user的cookie对象)
 *              4.得到对象的cookie对象的value(姓名与密码：userName-userPwd)
 *              5.通过split()方法将value字符串分割成数组
 *              6.从数组中分别得到对应的姓名与密码值
 *              7.请求转发到登录的操作  user?actionName=login&userName=姓名userPwd=密码
 *              8.return
 *
 *       如果以上判断都不满足，则拦截跳转到登录页面。
 *
 *
 *
 *
 */

@WebFilter("/*")
public class LoginAccessFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //基于HTTP
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;

        //得到访问的路径
        String path=request.getRequestURI();

        //1.指定页面,放行(用户无需登录的即可访问的资源:列如:登录页面login.jsp  注册页面register.jsp)
        if(path.contains("/login.jsp")){
            filterChain.doFilter(request,response);
            return;
        }

        // 2.静态资源,放行(存放在statics目录下的资源:列如js,css,images等)
        if(path.contains("/statics")){
            filterChain.doFilter(request,response);
            return;
        }

        //  3.指定行为,放行(用户无需登录即可执行的操作:列如:登录操作actionName=login等)
        if(path.contains("/user")){
            //得到用户行为
            String actionName=request.getParameter("actionName");
            //判断是否登录操作
            if("login".equals(actionName)){
                filterChain.doFilter(request,response);
                return;
            }


        }


        // 4.登录状态,放行(判断session作用域中是否存在user对象:存在则放行,不存在,则拦截跳转到登录页面)
        User user= (User) request.getSession().getAttribute("user");
        //判断user对象是否为空
        if(user!=null){
            filterChain.doFilter(request,response);
            return;
        }
        /**
         * 免登录(自动登录)
         *   从Cookie对象中获取用户的姓名与密码，自动执行登录操作
         */
        // 1.获取Cookie数组 request.getCookies();
        Cookie[] cookies=request.getCookies();
        // 2.判断Cookie数组
        if(cookies!=null&&cookies.length>0 ){
            // 3.便利Cookie数组，获取指定的Cookie对象（name为user的cookie对象)
            for(Cookie cookie:cookies){
                if("user".equals(cookie.getName())){
                    // 4.得到对象的cookie对象的value(姓名与密码：userName-userPwd)
                    String value=cookie.getValue();//admin-123456
                    // 5.通过split()方法将value字符串分割成数组
                    String[] val=value.split("-");
                    // 6.从数组中分别得到对应的姓名与密码值
                    String userName=val[0];
                    String userPwd=val[1];
                    // 7.请求转发到登录的操作  user?actionName=login&userName=姓名userPwd=密码,注：rem=1可以让不用重新登录
                    String url="user?actionName=login&rem=1&userName="+userName+"&userPwd="+userPwd;
                    request.getRequestDispatcher(url).forward(request,response);
                    // 8.return
                    return;
                }
            }
        }





        //拦截请求,重定向跳转到登录页面
        response.sendRedirect("login.jsp");
    }

    @Override
    public void destroy() {}




}
