package com.alibaba.cola.mock;

import com.alibaba.cola.mock.config.ColaMockPropertiesRegistrar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author shawnzhan.zxy
 * @date 2019/06/24
 */
@Configuration
@Import(ColaMockPropertiesRegistrar.class)
public class ColaMockAutoConfigure {

    //@Autowired
    //private ColaMockProperties properties;

    @Bean
    @ConditionalOnProperty(prefix = "cola.mock", value = "enabled", havingValue = "true")
    public ColaTestRecordController colaRecordController(@Autowired ColaMockProperties properties){
        ColaMockito.debugModel = properties.isDebugModel();
        ColaTestRecordController controller = new ColaTestRecordController(properties.getBasePackages());
        return controller;
    }

    @Bean
    @ConditionalOnProperty(prefix = "cola.mock", value = "enabled", havingValue = "true")
    public ColaMockGenerateController colaMockGenerateController(@Autowired ColaMockProperties properties){
        ColaMockGenerateController controller = new ColaMockGenerateController(properties.getBasePackages()[0]);
        return controller;
    }

}
