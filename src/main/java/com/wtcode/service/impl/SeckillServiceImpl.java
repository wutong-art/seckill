package com.wtcode.service.impl;

import com.wtcode.dao.SecKillDao;
import com.wtcode.dao.SuccessKilledDao;
import com.wtcode.dao.cache.RedisDao;
import com.wtcode.dto.Exposer;
import com.wtcode.dto.SeckillExecution;
import com.wtcode.entity.SecKill;
import com.wtcode.entity.SuccessKilled;
import com.wtcode.enums.SeckillStateEnum;
import com.wtcode.exception.RepeatKillException;
import com.wtcode.exception.SeckillCloseException;
import com.wtcode.exception.SeckillException;
import com.wtcode.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillDao secKillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //md5盐值字符串，用于混淆MD5
    private final String slat = "sdaWweqwe%$@dfs@!32fdpr(sss";

    public List<SecKill> getSeckillList() {
        return secKillDao.queryAll(0,4);
    }

    public SecKill getSeckillById(long seckillId) {
        return secKillDao.queryById(seckillId);
    }


    public Exposer exportSeckillUrl(long seckillId) {
        //优化点：缓存优化,一致性：超时的基础上维护
        //1.访问redis
        SecKill secKill = redisDao.getSeckill(seckillId);
        if(secKill==null){
            //2.访问数据库
            secKill = secKillDao.queryById(seckillId);
            if(secKill == null){
                return new Exposer(false,seckillId);
            }else{
                //3.放入redis
                redisDao.putSeckill(secKill);
            }
        }


        Date startTime = secKill.getStartTime();
        Date endTime = secKill.getEndTime();
        //当前系统时间
        Date nowTime = new Date();

        if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }

        //转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);

    }


    private String getMD5(long seckillId){
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    /**
     * 使用注解控制事务方法的优点
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC、HTTP请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
     */

    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5==null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存+记录购买行为
        Date nowTime = new Date();
        try {
            //减库存成功，记录购买行为
            int insertCount = successKilledDao.insertSuccessKill(seckillId,userPhone);
            if(insertCount<=0){
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            }else{
                //减库存，热点商品竞争
                int updateCount = secKillDao.reduceNumber(seckillId, nowTime);

                if(updateCount <= 0){
                    //没有更新到记录,秒杀结束，rollback
                    throw new SeckillCloseException("seckill is closed");
                }else {
                    //秒杀成功，commit
                    SuccessKilled successKilled = successKilledDao.qeuryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
            }

        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            //所有编译器异常，转化为运行期异常
            throw new SeckillException("seckill inner error");
        }
    }

    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5){
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        Date killtime = new Date();
        Map<String,Object> map = new HashMap<String, Object>();

        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killtime);
        map.put("result",null);
        //执行存储过程，result被赋值
        try{
            secKillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if(result == 1){
                SuccessKilled sk = successKilledDao.qeuryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,sk);
            }else {
                return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
        }

    }
}
