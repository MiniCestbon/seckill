package com.evian.seckill.utils;

import java.util.UUID;

/**
 * UUID工具类
 * 通用唯一识别码（Universally Unique Identifier）
 *
 * @author: Evian
 * @date 2022/5/2 13:41
 * @ClassName: UUIDUtil
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
