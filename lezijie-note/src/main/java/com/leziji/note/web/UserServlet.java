package com.leziji.note.web;


import com.leziji.note.po.User;
import com.leziji.note.service.UserSerivce;
import com.leziji.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/user")
@MultipartConfig
public class UserServlet extends HttpServlet {
    //重写HttpServlet中servics代码 快捷建Ctrl+O， 注意继承HttpServlet

    //web层会调用service层，建立一个对象
    private UserSerivce userSerivce=new UserSerivce();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse responser) throws ServletException, IOException {

        //设置首页导航高亮
        request.setAttribute("menu_page","user");


        //接受用户行为
        String actionName =request.getParameter("actionName");
        //判断用户行为，调用对应方法
        if("login".equals(actionName)){
            //用户登录
            userlogin(request,responser);

        }else if("logout".equals(actionName)){
            //用户退出
            userLogOut(request,responser);
        }else if("userCenter".equals(actionName)){
            //进入个人中心
            userCenter(request,responser);
        }else if("userHead".equals(actionName)){
            //加载头像
            userHead(request,responser);
        }else if("checkNick".equals(actionName)){
            //验证用户唯一性
            checkNick(request,responser);
        }else if("updateUser".equals(actionName)){
            //修改用户信息
            updateUser(request,responser);
        }

    }

    /**
     * 修改用户信息
     *  注:文件上次必须在Servlet类上添加注解!! @MultipartConfig
     *      1.调用Service层方法，传递request对象作为参数，返回resultInfo对象
     *      2.将resultInfo对象存到request作用域中
     *      3.请求转发跳转到个人中心（user？actionName=userCenter）
     * @param request
     * @param responser
     */

    private void updateUser(HttpServletRequest request, HttpServletResponse responser) throws ServletException, IOException {
        // 1.调用Service层方法，传递request对象作为参数，返回resultInfo对象
        ResultInfo<User> resultInfo=userSerivce.updateUser(request);
        // 2.将resultInfo对象存到request作用域中
        request.setAttribute("resultInfo",resultInfo);
        // 3.请求转发跳转到个人中心（user？actionName=userCenter）
        request.getRequestDispatcher("user?actionName=userCenter").forward(request,responser);
    }

    /**
     * 1.获取参数（昵称）
     * 2.从session作用域获取用户对象，得到用户ID
     * 3.调用Service层的方法，得到返回的结果
     * 4.通过字符输出流将结果响应给前台的aja的回调函数
     * 5.关闭资源
     * @param request
     * @param responser
     */
    private void checkNick(HttpServletRequest request, HttpServletResponse responser) throws IOException {
        //1.获取参数（昵称）
        String nick=request.getParameter("nick");
        //2.从session作用域获取用户对象，得到用户ID
        User user= (User) request.getSession().getAttribute("user");
        //3.调用Service层的方法，得到返回的结果
        Integer code=userSerivce.checkNick(nick,user.getUserId());
        //4.通过字符输出流将结果响应给前台的aja的回调函数
        responser.getWriter().write(code+"");
        //5.关闭资源
        responser.getWriter().close();
    }


    /**
     * 加载头像
     *     1.获取参数值（图面名称）
     *     2.得到图片的存放路径（request.getServletContext().getealPathR("/"))
     *     3.通过图面的完整路径 得到file对象
     *     4.通过截取，得到图片的后缀
     *     5.通过不同的图片后缀，设置不同的响应类型
     *     6，利用FileUtils的copyFile()方法，将图片拷贝给浏览器
     * @param request
     * @param responser
     */

    private void userHead(HttpServletRequest request, HttpServletResponse responser) throws IOException {
        //1.获取参数（图篇名称）
        String head =request.getParameter("imageName");
        // 2.得到图片的存放路径（request.getServletContext().getealPathR("/"))
        String realPath=request.getServletContext().getRealPath("/WEB-INF/upload/");
        // 3.通过图面的完整路径 得到file对象
        File file=new File(realPath+"/"+head);
        // 4.通过截取，得到图片的后缀
        String pic=head.substring(head.lastIndexOf(".")+1);
        // 5.通过不同的图片后缀，设置不同的响应类型
        if ("PNG".equalsIgnoreCase(pic)) {
            responser.setContentType("image/png");
        } else if ("JPG".equalsIgnoreCase(pic) || "JPEG".equalsIgnoreCase(pic)) {
            responser.setContentType("image/jpeg");
        } else if ("GIF".equalsIgnoreCase(pic)) {
            responser.setContentType("image/gif");
        }
        // 6，利用FileUtils的copyFile()方法，将图片拷贝给浏览器
        FileUtils.copyFile(file,responser.getOutputStream());
    }

    /**
     * 进入个人中心
     *  1.设置首页动态的页面值
     *  2.请求转发到index
     * @param request
     * @param responser
     */
    private void userCenter(HttpServletRequest request, HttpServletResponse responser) throws ServletException, IOException {
        //1.设置首页动态包含页面值
        request.setAttribute("changePage","user/info.jsp");
        //2.请求转发跳转到index
        request.getRequestDispatcher("index.jsp").forward(request,responser);
    }



    /**
     *  1.销毁Session对象
     *         2.删除Cookie对下那个
     *         3.重定向跳转到登录页面
     * @param request
     * @param responser
     */
    private void userLogOut(HttpServletRequest request, HttpServletResponse responser) throws IOException {
        // 1.销毁Session对象
        request.getSession().invalidate();
        // 2.删除Cookie对象
        Cookie cookie=new Cookie("user",null);
        cookie.setMaxAge(0);//设置0为删除cookie
        responser.addCookie(cookie);
        // 3.重定向跳转到登录页面
        responser.sendRedirect("login.jsp");
    }
    /**
     * 用户登录
     // 1.获取参数（姓名、mim)
        2.调用Service层的方法，返回ResultInfo对象
        3.判断是否登录成功
            如果失败
                将resultInfo对象设置到request作用域中
                请求转发到登录页面
            如果成功
                将用户信息设置到session作用域中
                判断用户是否选择记住密码（rem的值是1）
                如果是，将用户姓名与密码Cookie中，色织失效时间，并相应给客户段
                如果否，清空原有的cookie对象
            重定向到index页面
     */

    private void userlogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //这里的名字和前端名字一样
        //1.获取参数（姓名、mim)r
        String userName=request.getParameter("userName");
        String userPwd =request.getParameter("userPwd");

        ResultInfo<User> resultInfo=userSerivce.userLogin(userName,userPwd);
        // 2.调用Service层的方法，返回ResultInfo对象

        // 3.判断是否登录成功
        if(resultInfo.getCode()==1){//如果成功
            // 将用户信息设置到session作用域中
            request.getSession().setAttribute("user",resultInfo.getResult());
            // 判断用户是否选择记住密码（rem的值是1）
            String rem=request.getParameter("rem");
            // 如果是，将用户姓名与密码Cookie中，色织失效时间，并相应给客户段
            if("1".equals(rem)){
                //得到Cookie对象
                Cookie cookie=new Cookie("user",userName+"-"+userPwd);
                //设置失效时间
                cookie.setMaxAge(3*24*60*60);
                //响应给客户端
                response.addCookie(cookie);
            }else{
                //如果否，清空原有的cookie对象
                Cookie cookie=new Cookie("user","null");
                //删除cookie，设置maxage为0
                cookie.setMaxAge(0);
                //响应给客户端
                response.addCookie(cookie);

            }
            //重新定向到index页面
            response.sendRedirect("index");

        }else{//失败

            //  将resultInfo对象设置到request作用域中
            request.setAttribute("resultInfo",resultInfo);
            //  请求转发到登录页面
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }

    }
}
