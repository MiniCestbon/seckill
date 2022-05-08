package com.evian.seckill.config;

import com.evian.seckill.pojo.User;

/**
 * @author: Evian
 * @date 2022/5/7 21:56
 * @ClassName: UserContext
 */
public class UserContext {

    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user) {
        userThreadLocal.set(user);
    }

    public static User getUser() {
        return userThreadLocal.get();
    }
}
