package com.leziji.note.vo;

import lombok.Getter;
import lombok.Setter;

/*
封装返回结果的类
    状态码
        成功=1，失败=0
    提示对象
    返回的对象（字符串，javaBean、集合、Map等）
 */

//调用get和set方法
@Getter
@Setter

//准备一个范型
public class ResultInfo<T> {
    private Integer code; //状态码
    private String msg;   //提示信息
    private T result;     //返回的对象
}
