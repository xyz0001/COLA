package com.alibaba.cola.mock;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.cola.mock.autotest.SpringMockitoTestXmlGenerator;
import com.alibaba.cola.mock.autotest.SuperClassGenerator;
import com.alibaba.cola.mock.middleware.FilterManager;
import com.alibaba.cola.mock.proxy.DataRecordProxy;
import com.alibaba.cola.mock.utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.PriorityOrdered;

import static com.alibaba.cola.mock.utils.ClassUtils.getClassName;

/**
 * 数据录制控制器
 * @author shawnzhan.zxy
 * @date 2019/06/30
 */
public class ColaMockGenerateController implements BeanPostProcessor,BeanDefinitionRegistryPostProcessor,PriorityOrdered
    ,ApplicationListener<ContextRefreshedEvent>{
    private static final Logger logger = LoggerFactory.getLogger(ColaMockGenerateController.class);

    @Autowired
    private ColaMockProperties properties;
    protected BeanDefinitionRegistry registry;
    private String basePackage;
    //中间件拦截器
    private FilterManager filterManager = new FilterManager();
    private Set<Class> mocks = new HashSet<>();
    SpringMockitoTestXmlGenerator xmlGenerator = new SpringMockitoTestXmlGenerator();
    SuperClassGenerator superClassGenerator = new SuperClassGenerator();
    private boolean existsSpringTestXml = false;

    public ColaMockGenerateController(String basePackage){
        this.basePackage = basePackage;
        existsSpringTestXml = isExistsSpringTestXml();
        if(!existsSpringTestXml) {
            filterManager.loadFilterPlugins(basePackage);
        }

        logger.info("ColaMockGenerateController init. existsSpringTestXml=" + existsSpringTestXml);
    }

    private boolean isExistsSpringTestXml(){
        File file = new File(xmlGenerator.getOutputFilePath());
        return file.exists();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(!valid(bean, beanName)){
            return bean;
        }

        BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
        String metaClassName = getClassName(bean, beanDefinition);
        try {
            Class factCls = Class.forName(metaClassName);
            if(!filterManager.doFilter(bean, beanDefinition)){
                return bean;
            }
            mocks.add(factCls);
            bean = DataRecordProxy.createProxy2PoolWithManual(factCls, bean, beanName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    private boolean valid(Object bean, String beanName){
        if(existsSpringTestXml){
            return false;
        }
        if(beanName.indexOf(Constants.INNER_BEAN_NAME) > -1) {
            return false;
        }
        if (bean instanceof FactoryBean) {
            return false;
        }
        if(bean.getClass().getName().indexOf(Constants.COLAMOCK_PROXY_FLAG) > -1){
            return false;
        }
        if(!registry.containsBeanDefinition(beanName)){
            return false;
        }
        return true;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(existsSpringTestXml){
            return;
        }
        logger.info("generating SpringBasetTest and spring-mockito-test.xml");
        xmlGenerator.putParam("basePackage", basePackage)
            .generate();
        superClassGenerator
            .putParam("mocks", Arrays.asList(mocks.toArray()))
            .putParam("basePackage", basePackage)
            .generate();

        //Class templateSuperClass = ClassUtils.compileAndLoadClass(superClassGenerator.getOutputFilePath());
        //ColaTestRecordController.templateSuperClass = templateSuperClass;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
