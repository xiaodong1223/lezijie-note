package com.leziji.note.po;
/*
    User对应字段看数据库
 */

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/*
@Getter和Setter可以不用写get和set方法
    使用步骤
        1.xml文件中引入依赖
           <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <version>1.18.12</version>
              <scope>provided</scope>
           </dependency>
        2.file->setting->plugins中下载
 */

public class User {
    private  Integer userId;  //用户id
    private  String uname;    //用户名称
    private String upwd;     //用户密码

    private  String nick;     //用户昵称
    private  String head;     //用户头像
    private  String mood;     //用户签名



}