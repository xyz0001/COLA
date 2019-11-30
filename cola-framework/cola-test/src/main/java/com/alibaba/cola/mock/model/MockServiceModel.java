package com.alibaba.cola.mock.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * @author shawnzhan.zxy
 * @date 2018/09/02
 */
public class MockServiceModel implements Serializable{
    /** 服务接口*/
    private Class<?> interfaceCls;
    /** 服务名*/
    private String serviceName;
    /** 被mock实例*/
    private Object target;
    /** mock后生成的实例*/
    private Object mock;
    /** 是否人工标记*/
    private boolean manual = false;
    /** 是否最低层叶子mock*/
    private boolean leaf = true;

    public MockServiceModel(){}
    public MockServiceModel(Class<?> interfaceCls, String serviceName, Object target
        , Object mock){
        this.interfaceCls = interfaceCls;
        this.serviceName = serviceName;
        this.target = target;
        this.mock = mock;
    }

    public MockServiceModel(Class<?> interfaceCls, String serviceName, Object target
        , Object mock, boolean manual, boolean leaf){
        this(interfaceCls, serviceName, target, mock);
        this.manual = manual;
    }

    public Class<?> getInterfaceCls() {
        return interfaceCls;
    }

    public void setInterfaceCls(Class<?> interfaceCls) {
        this.interfaceCls = interfaceCls;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getMock() {
        return mock;
    }

    public void setMock(Object mock) {
        this.mock = mock;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    //public boolean isSpyMockito() {
    //    return spyMockito;
    //}

    //public void setSpyMockito(boolean spyMockito) {
    //    this.spyMockito = spyMockito;
    //}

    @Override
    public boolean equals(Object obj) {
        MockServiceModel comp = (MockServiceModel)obj;
        if(StringUtils.isNotBlank(this.serviceName) && this.serviceName.equals(comp.getServiceName())){
            return true;
        }else if(this.interfaceCls.equals(comp.getInterfaceCls())){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(StringUtils.isNotBlank(this.serviceName)){
            return this.serviceName.hashCode();
        }else{
            return this.interfaceCls.hashCode();
        }
    }

    @Override
    public MockServiceModel clone() throws CloneNotSupportedException {
        MockServiceModel model = new MockServiceModel(interfaceCls, serviceName, target, mock);
        return model;
    }

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
}
