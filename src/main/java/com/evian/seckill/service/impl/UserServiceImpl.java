package com.evian.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evian.seckill.exception.GlobalException;
import com.evian.seckill.mapper.UserMapper;
import com.evian.seckill.pojo.User;
import com.evian.seckill.service.IUserService;
import com.evian.seckill.utils.CookieUtil;
import com.evian.seckill.utils.MD5Util;
import com.evian.seckill.utils.UUIDUtil;
import com.evian.seckill.utils.ValidatorUtil;
import com.evian.seckill.vo.LoginVo;
import com.evian.seckill.vo.RespBean;
import com.evian.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author evian
 * @since 2022-05-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        // 校验输入
//        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
            throw new GlobalException(RespBeanEnum.MOBILE_ERROR);
        }
        // 查询数据库
        User user = userMapper.selectById(mobile);
        if(null == user){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // cookie session
        String cookie = UUIDUtil.uuid();
//        request.getSession().setAttribute(cookie, user);
        redisTemplate.opsForValue().set("user: " + cookie, user);

        CookieUtil.setCookie(request, response, "userTicket", cookie);

        return RespBean.success();
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(userTicket == null){
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user: " + userTicket);
        if(user != null){
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int result = userMapper.updateById(user);
        if (1 == result) {
            //删除Redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
