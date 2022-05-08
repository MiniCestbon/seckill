package com.evian.seckill.exception;

import com.evian.seckill.vo.RespBean;
import com.evian.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author xs
 * @date 2022/5/2 12:05
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @ExceptionHandler 拦截异常，通过该注解实现自定义异常处理。value 指定需要拦截的异常类型
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e){
        if(e instanceof GlobalException){
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            respBean.setMessage("参数校验异常： " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
