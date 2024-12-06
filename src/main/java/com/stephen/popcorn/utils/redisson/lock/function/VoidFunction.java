package com.stephen.popcorn.utils.redisson.lock.function;

/**
 * 分布式锁中所用到的函数式接口
 *
 * @author stephen qiu
 */
@FunctionalInterface
public interface VoidFunction {
	
	void method();
	
}