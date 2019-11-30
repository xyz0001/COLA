package com.alibaba.cola.mock.spring;

import com.alibaba.cola.mock.ColaMockito;
import com.alibaba.cola.mock.utils.Constants;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * @author shawnzhan.zxy
 * @date 2019/07/01
 */
public class ColaMockPropertiesResolve implements EnvironmentAware {
    private ConfigurableEnvironment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment)environment;
        String debugModel = environment.getProperty("cola.mock.debug-model");
        ColaMockito.debugModel = Constants.TRUE.equals(debugModel);
    }
}
