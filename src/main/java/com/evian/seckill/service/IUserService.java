package com.evian.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evian.seckill.pojo.User;
import com.evian.seckill.vo.LoginVo;
import com.evian.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author evian
 * @since 2022-05-01
 */
public interface IUserService extends IService<User> {
    /**
     * 登录
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @return
     */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    /**
     * 修改密码
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
