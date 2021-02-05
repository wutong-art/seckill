package com.wtcode.dao;

import com.wtcode.entity.SecKill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SecKillDao {

    /**
     * 减库存
     * @param seckillId
     * @param KillTime
     * @return 如果影响行数=1，表示更新的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date KillTime);

    /**
     * 根据秒杀id查询库存
     * @param seckillId
     * @return
     */
    SecKill queryById(long seckillId);

    /**
     * 查询偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<SecKill> queryAll(@Param("offset") int offset,@Param("limit") int limit);

    /**
     * 使用存储过程执行秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String,Object> paramMap);


}
