package com.alibaba.cola.mock;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.cola.mock.model.ColaTestModel;
import com.alibaba.cola.mock.model.MockServiceModel;
import com.alibaba.cola.mock.persist.ServiceListStore;
import com.alibaba.cola.mock.proxy.MockDataProxy;
import com.alibaba.cola.mock.scan.AutoMockFactoryBean;
import com.alibaba.cola.mock.scan.FilterManager;
import com.alibaba.cola.mock.utils.CommonUtils;
import com.alibaba.cola.mock.utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 集成测试回放控制器
 * @author shawnzhan.zxy
 * @date 2018/10/08
 */
public class ColaMockController implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor, InitializingBean
    ,ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ColaMockController.class);
    protected static ConfigurableListableBeanFactory beanFactory;
    private static BeanDefinitionRegistry registry;
    /**
     * Config basePackage.
     */
    private List<String> serviceList;
    private FilterManager monitorFilterManager;
    private ServiceListStore serviceListStore = new ServiceListStore();

    public ColaMockController(String... basePackages){
        serviceList = serviceListStore.load();
        List<ColaTestModel> colaTestModelList = ColaMockito.g().scanColaTest(basePackages);
        ColaMockito.g().getContext().setColaTestModelList(colaTestModelList);
        monitorFilterManager = new FilterManager();
        colaTestModelList.forEach(p->{
            monitorFilterManager.addAll(p.getTypeFilters());
        });
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ColaMockController.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ColaMockController.beanFactory = beanFactory;
        ColaMockito.g().setBeanFactory(beanFactory);
        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        Map<String, String> index = Arrays.stream(beanDefinitionNames).collect(Collectors.toMap(key->key, v->v));
        for(String name : serviceList){
            String[] meta = name.split(Constants.SERVICE_LIST_DELIMITER);
            String beanName = meta[0];
            String beanClass = meta[1];
            if(index.get(meta[0]) == null){
                try {
                    Class type = Thread.currentThread().getContextClassLoader().loadClass(beanClass);
                    GenericBeanDefinition definition = new GenericBeanDefinition();
                    definition.getConstructorArgumentValues().addGenericArgumentValue(beanName);
                    definition.getConstructorArgumentValues().addGenericArgumentValue(type);
                    definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                    definition.setBeanClass(AutoMockFactoryBean.class);
                    definition.setScope("singleton");
                    definition.setLazyInit(true);
                    definition.setAutowireCandidate(true);
                    registry.registerBeanDefinition(beanName, definition);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(putPoolIfColaProxy(bean, beanName)){
            return bean;
        }
        if(monitorFilterManager.match(bean.getClass())){
            bean = MockDataProxy.createProxy2PoolWithManual(bean.getClass(), bean, beanName);
        }

        return bean;
    }

    private boolean putPoolIfColaProxy(Object bean, String beanName){
        if(bean.getClass().getName().indexOf(Constants.COLAMOCK_PROXY_FLAG) < 0){
            return false;
        }
        //此处可能有问题，跟踪一下逻辑，看能不能把putMonitorMock全收拢了，目前在AutoMockFactoryBean里也有这个逻辑
        MockServiceModel mockModel = null;
        if(monitorFilterManager.match(bean.getClass())){
            mockModel = new MockServiceModel(bean.getClass(), beanName, null, bean, true, false);
        }else{
            mockModel = new MockServiceModel(bean.getClass(), beanName, null, bean);
        }
        ColaMockito.g().getContext().putMonitorMock(mockModel);
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {}

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        CommonUtils.printMockObjectList();
    }
}
