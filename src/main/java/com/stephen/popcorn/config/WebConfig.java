package com.stephen.popcorn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: stephen qiu
 * @create: 2024-03-16 20:31
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:5173") // 允许跨域请求的源
				.allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的请求方法
				.allowedHeaders("Content-Type", "Authorization") // 允许的请求头
				.allowCredentials(true); // 允许携带凭据
	}
}
