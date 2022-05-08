package com.evian.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evian.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 *
 * @author evian
 * @since 2022-05-01
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
