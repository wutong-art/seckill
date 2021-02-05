package com.wtcode.dao;

import com.wtcode.entity.SecKill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合，junit启动时加载springIOC容器
 * spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)//spring启动时加载IOC容器
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SecKillDaoTest {


    //注入dao实现类依赖
    @Resource
    private SecKillDao secKillDao;

    @Test
    public void testQueryById() throws Exception{
        long id = 1000;
        SecKill secKill = secKillDao.queryById(id);
        System.out.println(secKill.getName());
        System.out.println(secKill);
        //4000元秒杀iphone12
        //SecKill{seckillId=1000, name='4000元秒杀iphone12', number=100, startTime=Tue Dec 22 00:00:00 GMT+08:00 2020, endTime=Wed Dec 23 00:00:00 GMT+08:00 2020, createTime=Mon Dec 21 20:38:32 GMT+08:00 2020}
    }

    @Test
    public void testQueryAll() throws Exception{
        List<SecKill> secKills = secKillDao.queryAll(0, 100);
        System.out.println(secKills);
    }

    @Test
    public void testReduceNumber() throws Exception{
        Date killtime = new Date();
        int updatecount = secKillDao.reduceNumber(1000L, killtime);
        System.out.println(updatecount);
    }
}
