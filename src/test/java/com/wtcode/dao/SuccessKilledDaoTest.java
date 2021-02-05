package com.wtcode.dao;

import com.wtcode.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKill() throws Exception{
        long id = 1003L;
        long phone = 13546304469L;
        int i = successKilledDao.insertSuccessKill(id, phone);
        System.out.println(i);
    }
    @Test
    public void testQeuryByIdWithSeckill() throws Exception{
        long id =1003L;
        long phone = 13546304469L;
        SuccessKilled successKilled = successKilledDao.qeuryByIdWithSeckill(id, phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());

    }

}