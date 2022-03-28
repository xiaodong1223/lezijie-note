package com.lezijie.note;


import com.leziji.note.util.DBUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDB {
    //使用日志工程类，记录日志
    private Logger logger= LoggerFactory.getLogger(TestDB.class);

    /*
    单元测试方法
        1.方法的返回值，使用void，一般没有返回值
        2.参数列表，建议空参，没有参数
        3.方法上需要色织@Test注释
        4.美国方法都能独立运行


        判定结果：
            绿色：成功
            红色：失败
     */
    //@Test是让他能够执行
    @Test
    public void testDB(){
        System.out.println(DBUtil.getConnetion());
        //使用日志
       logger.info("获取数据库连接"+DBUtil.getConnetion());
       //另一种写法  获取的数据在{位置}
        logger.info("获取数{}据库连接:",DBUtil.getConnetion());
    }
}
