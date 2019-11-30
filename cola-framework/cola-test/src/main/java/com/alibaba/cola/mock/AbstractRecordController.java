package com.alibaba.cola.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.alibaba.cola.mock.model.ServiceModel;
import com.alibaba.cola.mock.persist.ServiceListStore;
import com.alibaba.cola.mock.utils.CommonUtils;
import com.alibaba.cola.mock.utils.Constants;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author shawnzhan.zxy
 * @date 2018/10/28
 */
public abstract class AbstractRecordController implements BeanPostProcessor,BeanDefinitionRegistryPostProcessor
    ,ApplicationListener<ContextRefreshedEvent>,InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRecordController.class);

    private static final Integer UN_START = 0;
    private static final Integer STARTING = 1;
    private static final Integer END = 2;

    protected BeanDefinitionRegistry registry;
    protected ConfigurableListableBeanFactory beanFactory;
    protected Set<ServiceModel> serviceSet = new HashSet<>();
    protected ServiceListStore serviceListStore = new ServiceListStore();
    private static AtomicInteger INIT_FLAG = new AtomicInteger(0);

    public AbstractRecordController(){
        if(!INIT_FLAG.compareAndSet(UN_START, STARTING)){
            throw new RuntimeException("init is running!");
        }
    }
    public abstract void init();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!INIT_FLAG.compareAndSet(STARTING, END)){
            return;
        }
        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        for(String beanName : beanDefinitionNames){
            try {
                beanFactory.getBean(beanName);
            }catch (Throwable e){}
        }

        if(serviceSet.size() == 0){
            return;
        }
        List<ServiceModel> serviceList = new ArrayList<>(serviceSet);
        Map<String, ServiceModel> index = new HashMap<>();
        serviceList.forEach(p->{
            ServiceModel serviceModel = index.get(p.getServiceName());
            if(serviceModel == null){
                index.put(p.getServiceName(), p);
                return;
            }
            //如果出现重复，只保留一个
            if(serviceModel.isFactory()){
                index.put(p.getServiceName(), p);
            }
        });
        List<String> serviceStrList = index.entrySet().stream().map(set->set.getValue().getServiceName() + Constants.SERVICE_LIST_DELIMITER
            + set.getValue().getServiceCls().getName()).collect(Collectors.toList());
        List<String> oriServiceList = serviceListStore.load();
        Collections.sort(oriServiceList);
        Collections.sort(serviceStrList);
        String serviceStr = StringUtils.join(serviceStrList.iterator(), ",");
        String oriServiceStr = StringUtils.join(oriServiceList.iterator(), ",");
        if(!serviceStr.equals(oriServiceStr)){
            serviceListStore.clean();
            serviceListStore.save(serviceStrList);
        }

        CommonUtils.printMockObjectList();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        ColaMockito.g().setBeanFactory(beanFactory);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }


}
