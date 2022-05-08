package com.evian.seckill.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author xs
 * @date 2022/5/1 23:07
 */
@Data
public class LoginVo {
    @NotNull
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;
}
