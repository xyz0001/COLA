package com.alibaba.cola.mock.middleware;

import org.springframework.beans.factory.config.BeanDefinition;

/**
 * @author shawnzhan.zxy
 * @date 2019/07/11
 */
public abstract class AbstractFilter {

    public abstract boolean doFilter(Object bean, BeanDefinition beanDefinition);
}
