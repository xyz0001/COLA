package com.alibaba.cola.mock;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author shawnzhan.zxy
 * @date 2019/06/24
 */
@ConfigurationProperties(prefix = "cola.mock")
public class ColaMockProperties {

    private String[] basePackages;
    /** 调试模式-开启更全面的日志信息*/
    private boolean debugModel;

    public String[] getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }

    public boolean isDebugModel() {
        return debugModel;
    }

    public void setDebugModel(boolean debugModel) {
        this.debugModel = debugModel;
    }
}
