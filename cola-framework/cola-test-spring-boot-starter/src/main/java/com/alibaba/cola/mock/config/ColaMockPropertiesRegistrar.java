package com.alibaba.cola.mock.config;

import com.alibaba.cola.mock.ColaMockProperties;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author shawnzhan.zxy
 * @date 2019/07/01
 */
@Configuration
//@EnableConfigurationProperties({ColaMockProperties.class})
public class ColaMockPropertiesRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private ConfigurableEnvironment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment)environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ColaMockProperties properties = BinderUtils.bind(environment, "cola.mock", ColaMockProperties.class);
        registry.registerBeanDefinition("colaMockProperties", createBeanDefinition(properties));
    }

    private BeanDefinition createBeanDefinition(ColaMockProperties properties) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ColaMockProperties.class)
            .addPropertyValue("basePackages", properties.getBasePackages());
        return builder.getBeanDefinition();

    }
}
