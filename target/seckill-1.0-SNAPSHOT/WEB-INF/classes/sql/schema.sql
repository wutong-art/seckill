#数据库初始化脚本
#创建数据库
CREATE DATABASE seckill;
#使用数据库
USE seckill;
#创建秒杀库存表
CREATE TABLE seckill(
	`seckill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
	`name` VARCHAR(120) NOT NULL COMMENT '商品名称',
	`number` INT NOT NULL COMMENT '库存数量',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`start_time` TIMESTAMP NOT NULL COMMENT '秒杀开启时间',
	`end_time` TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
	PRIMARY KEY (seckill_id),
	KEY idx_start_time(start_time),
	KEY idx_end_time(end_time),
	KEY idx_create_time(create_time)
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

#初始化数据
INSERT INTO seckill(NAME,number,start_time,end_time) 
VALUES  ('4000元秒杀iphone12',100,'2020-12-22 00:00:00','2020-12-23 00:00:00'),
	('2000元秒杀ipad5',100,'2020-12-22 00:00:00','2020-12-23 00:00:00'),
	('4000元秒杀小米10',100,'2020-12-22 00:00:00','2020-12-23 00:00:00'),
	('7000元秒杀外星人',100,'2021-01-01 00:00:00','2020-01-23 00:00:00');
UPDATE seckill SET start_time='2021-01-02 00:00:00',end_time = '2021-01-29 00:00:00' WHERE seckill_id = 1003;
SELECT * FROM seckill;
#秒杀成功明细表
#用户登录认证相关的信息
CREATE TABLE success_killed(
	`seckill_id` BIGINT NOT NULL COMMENT '秒杀商品id',
	`user_phone` BIGINT NOT NULL COMMENT '用户手机号',
	`state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识 -1:无效 0：成功 1：已付款 2：已发货',
	`create_time` TIMESTAMP NOT NULL COMMENT '创建时间',
	PRIMARY KEY(seckill_id,user_phone),/*联合主键 */	
	KEY idx_create_time(create_time)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表'


#记录每次上线的DDL修改
ALTER TABLE seckill
DROP INDEX idx_create_time,
ADD INDEX idx_c_s(start_time,create_time);




