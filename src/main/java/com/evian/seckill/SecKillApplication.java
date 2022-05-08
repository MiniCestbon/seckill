package com.evian.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xs
 * @date 2022/4/30
 */
@SpringBootApplication
@MapperScan("com.evian.seckill.mapper")
public class SecKillApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecKillApplication.class, args);
	}

}
