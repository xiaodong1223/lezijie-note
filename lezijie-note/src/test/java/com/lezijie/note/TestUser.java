package com.lezijie.note;

import com.leziji.note.dao.BaseDao;
import com.leziji.note.dao.UserDao;
import com.leziji.note.po.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class TestUser {
    //@Test可以执行
    @Test
    public void TestQueryUserByName(){
        UserDao userDao=new UserDao();
        User user=userDao.queryUserByName("admin");
        System.out.println(user.getUpwd());

    }

    @Test
    public void testAdd(){
        String sql="insert into tb_user(uname,upwd,nick,head ,mood)values(?,?,?,?,?)";
        List<Object> params=new ArrayList<>();
        params.add("lisi");
        params.add("e10adc3949ba59abbe56e057f20f883e");
        params.add("lisi");
        params.add("404.jpg");
        params.add("Hello");
        int row= BaseDao.executeUpdate(sql,params);
        System.out.println(row);
    }
}
