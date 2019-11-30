package com.alibaba.cola.mock.middleware;

import java.lang.reflect.Proxy;

import com.alibaba.cola.mock.spring.MapperFactoryBean;

import org.springframework.beans.factory.config.BeanDefinition;

/**
 * @author shawnzhan.zxy
 * @date 2019/06/25
 */
public class MybatisFilter extends AbstractFilter{

    @Override
    public boolean doFilter(Object bean, BeanDefinition beanDefinition){
        if(!Proxy.isProxyClass(bean.getClass())){
            return false;
        }
        if(MapperFactoryBean.class.getName().equals(beanDefinition.getBeanClassName())){
            return true;
        }else{
            return false;
        }
    }
}
