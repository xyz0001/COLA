package com.alibaba.cola.mock.middleware;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.cola.mock.ColaMockGenerateController;
import com.alibaba.cola.mock.scan.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * @author shawnzhan.zxy
 * @date 2019/07/11
 */
public class FilterManager extends AbstractFilter{
    private static final Logger logger = LoggerFactory.getLogger(FilterManager.class);

    private static Set<AbstractFilter> filterList = new HashSet<>();

    @Override
    public boolean doFilter(Object bean, BeanDefinition beanDefinition) {
        boolean find = false;
        for(AbstractFilter filter : filterList){
            find = filter.doFilter(bean, beanDefinition);
            if(find){
                return find;
            }
        }
        return find;
    }

    public void register(AbstractFilter filter){
        filterList.add(filter);
    }

    public void loadFilterPlugins(String... basePackages){
        this.register(new HsfFilter());
        this.register(new MybatisFilter());

        ClassScanner classScanner = new ClassScanner();
        try {
            List<Class> pluginList = classScanner.scanForSupper(AbstractFilter.class, basePackages);
            for(Class pluginClazz : pluginList){
                this.register((AbstractFilter)pluginClazz.newInstance());
            }
        } catch (Exception e) {
            logger.error("loadFilterPlugins ERROR!", e);
        }
    }
}
