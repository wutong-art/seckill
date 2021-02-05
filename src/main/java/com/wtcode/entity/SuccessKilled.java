package com.wtcode.entity;

import java.util.Date;

public class SuccessKilled {
    private long seckill_Id;
    private long userPhone;
    private short state;
    private Date createTime;

    //多对一，多个秒杀成功信息对应一个秒杀
    private SecKill seckill;

    public long getSeckill_Id() {
        return seckill_Id;
    }

    public void setSeckill_Id(long seckill_Id) {
        this.seckill_Id = seckill_Id;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public SecKill getSeckill() {
        return seckill;
    }

    public void setSeckill(SecKill seckill) {
        this.seckill = seckill;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckill_Id=" + seckill_Id +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }
}
