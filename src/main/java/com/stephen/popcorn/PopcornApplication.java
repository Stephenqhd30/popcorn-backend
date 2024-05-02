package com.stephen.popcorn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author stephen qiu
 * @MapperScan("com.stephen.popcorn.mapper") 自动扫描mapper
 * @EnableScheduling 开启定时任务
 */
@SpringBootApplication
@MapperScan("com.stephen.popcorn.mapper")
@EnableScheduling
public class PopcornApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PopcornApplication.class, args);
	}
	
}
