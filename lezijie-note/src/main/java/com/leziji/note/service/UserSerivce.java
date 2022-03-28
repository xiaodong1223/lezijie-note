package com.leziji.note.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.leziji.note.dao.UserDao;
import com.leziji.note.po.User;
import com.leziji.note.vo.ResultInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

/*
UserSerivce中通常回调用UserDao

dao层写完写serivce层

Service层：（业务逻辑层：参数判断、业务逻辑处理）
   1.判断参数是否为空
       如果为空
           设置ResultInfo对象的状态码和提示信息
           返回resultInfo对象
   2.如果不为空，通过用户名查询用户对象
   3.判断用户对象是否为空
        如果为空
           设置ResultInfo对象的状态码和提示信息
           返回resultInfo对象
   4.如果用户对象不为空，将数据库中查询到的用户对象的密码与前台传递的密码作比较（将密码加密后在比较）
        如果密码不正确
            设置ResultInfo对象的状态码和提示信息
            将ResultInfo对象设置request作用域中请求转发跳转到登录页面
            return
   5.如果密码正确
        设置ResultInfo对象的状态码和提示信息
   6.返回resultInfo对象


 */
public class UserSerivce {

    private UserDao userDao=new UserDao();

    public ResultInfo<User> userLogin(String userName, String userPwd) {
        ResultInfo<User> resultInfo=new ResultInfo<>();

        //数据回显：当登录实现时，将登录信息返回给页面显示
        User u=new User();
        u.setUname(userName);
        u.setUpwd(userPwd);

        //设置到resultInfo对象中
        resultInfo.setResult(u);

        //1.判断参数是否为空
        //注：hutool工具集中有StrUTil.isBlank方法判断为空
        if(StrUtil.isBlank(userName)||StrUtil.isBlank(userPwd)){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户名或密码不能为空");
            return resultInfo;
        }

        //2.如果不为空，通过用户名查询用户对象
        User user=userDao.queryUserByName(userName);

        //3.判断用户对象是否为空
        if(user==null){
        // 如果为空，设置ResultInfo对象的状态码和提示信息
            resultInfo.setCode(0);
            resultInfo.setMsg("该用户不存在");
        // 返回resultInfo对象
            return  resultInfo;
        }

        // 4.如果用户对象不为空，将数据库中查询到的用户对象的密码与前台传递的密码作比较（将密码加密后在比较）
        //将前台传递的密码按照HD5算法加密
        //hutool工具集中有DigesUtil.md5Hex()加密算法
        userPwd= DigestUtil.md5Hex(userPwd);
        //判断加密后的密码是否与数据库一致
        if(!userPwd.equals(user.getUpwd())){
            // 如果密码不正确
            resultInfo.setCode(0);
            // 设置ResultInfo对象的状态码和提示信息
            // 将ResultInfo对象设置request作用域中请求转发跳转到登录页面
            // return
            resultInfo.setMsg("用户密码不正确");
            return resultInfo;
        }
        //如果密码正确
        resultInfo.setCode(1);


        //设置ResultInfo对象的状态码和提示信息
        resultInfo.setResult(user);
        //6.返回resultInfo对象
        return resultInfo;

    }


    /**
     *验证昵称的唯一性
     *  1.判断按昵称是否为空
     *      如果为空，返回"0"
     *  2.调用Dao层，通过用户ID和昵称查询用户对象
     *  3.判断用户对象存在
     *      存在,返回”0“
     *      不存在，返回“1”
     * @param nick
     * @param userId
     * @return
     */

    public Integer checkNick(String nick, Integer userId) {
        //1.判断昵称是否为空
        if(StrUtil.isBlank(nick)){
            return 0;
        }
        //2.调用Dao层，通过用户ID和昵称查询用户对象
        User user= userDao.queryUserByNickAndUserId(nick,userId);

        //3.判断用户对象存在
        if(user!=null){
            return 0;
        }
        return 1;
    }

    /**
     * 修改用户信息
     *      1.获取参数(昵称，心情)
     *      2.参数的非空校验(判断比太难参数非空)
     *          如果昵称为空，将状态码和错误信息设置resultInfo对象中，返回resultInfo对象
     *      3.从session作用域中获取用户对象(获取用户对象中默认头像)
     *      4.实现上传文件的操作
     *          1.获取Part对象 request.getpart("name");name代表的file文件域属性值
     *          2.通过Part对象获取上传文件的文件名
     *          3.判断文件名是否为空
     *          4.获取文件存放的路径  WEB-INF/upload/目录中
     *          5.上传文件到指定目录
     *      5.更新用户头像（将原本用户对象中默认头像设置为上传文件名）
     *      6.调用Dao层的更新方法，返回受影响的行数
     *      7.判断受影响的行数
     *          如果大于零修改是成功，否则反之
     *      8.返回resultInfo对象
     * @param request
     * @return
     */
    public ResultInfo<User> updateUser(HttpServletRequest request) {
        ResultInfo<User> resultInfo=new ResultInfo<>();
        // 1.获取参数(昵称，心情)
        String nick=request.getParameter("nick");
        String mood=request.getParameter("mood");
        // 2.参数的非空校验(判断比太难参数非空)
        if(StrUtil.isBlank(nick)){
            // 如果昵称为空，将状态码和错误信息设置resultInfo对象中，返回resultInfo对象
            resultInfo.setCode(0);
            resultInfo.setMsg("用户昵称不能为空");
            return resultInfo;
        }

        // 3.从session作用域中获取用户对象(获取用户对象中默认头像)
        User user= (User) request.getSession().getAttribute("user");
        //设置修改的昵称和头像
        user.setNick(nick);
        user.setMood(mood);

        //4.实现上传文件的操作
        try{
            //1.获取Part对象 request.getpart("name");name代表的file文件域属性值
            Part part=request.getPart("img");
            //2.通过Part对象获取上传文件的文件名
            String header=part.getHeader("Content-Disposition");
            //获取具体的请求头对应的值
            String str=header.substring(header.lastIndexOf("=")+2);
            //获取上传的文件名
            String  fileName=str.substring(0,str.length()-1);
            //3.判断文件名是否为空
            if(!StrUtil.isBlank(fileName)){
                //如果用户上传了头像，则更新用户对象的中头像
                user.setHead(fileName);
                //4.获取文件存放的路径  WEB-INF/upload/目录中
                String filePath=request.getServletContext().getRealPath("WEB-INF/upload");
                //5.上传文件到指定目录
                part.write(filePath+"/"+fileName);
            }


        }catch(Exception e){
           e.printStackTrace();
        }

        //调用Dao层的更新方法，返回受影响的行数
        int row=userDao.updateUser(user);
        //判断受影响的行数
        if(row>0){
            resultInfo.setCode(1);
            //更新session中用户对象
            request.getSession().setAttribute("user",user);
        }else{
            resultInfo.setCode(0);
            resultInfo.setMsg("更新失败！");
        }

        return  resultInfo;
    }
}
