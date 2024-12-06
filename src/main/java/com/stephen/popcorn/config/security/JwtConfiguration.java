package com.stephen.popcorn.config.security;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import com.stephen.popcorn.config.security.condition.JwtCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * SaToken是否使用Jwt
 *
 * @author stephen qiu
 */
@Configuration
@Conditional(JwtCondition.class)
@Slf4j
public class JwtConfiguration {
	
	/**
	 * Sa-Token 整合 jwt (该模板使用 Simple 简单模式，一共有三种模式，
	 * 详情见：<a href="https://sa-token.cc/doc.html#/plugin/jwt-extend">...</a>)
	 */
	@Bean
	public StpLogic getStpLogicJwt() {
		return new StpLogicJwtForSimple();
	}
	
	/**
	 * 依赖注入日志输出
	 */
	@PostConstruct
	private void initDi() {
		log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
	}
	
}