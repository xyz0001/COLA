package com.alibaba.cola.mock.spring;

import com.alibaba.cola.mock.agent.logger.Logger;
import com.alibaba.cola.mock.agent.logger.LoggerFactory;
import com.alibaba.cola.mock.proxy.MockDataProxy;
import com.alibaba.cola.mock.utils.MockHelper;

import org.mockito.Mockito;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author shawnzhan.zxy
 * @date 2019/04/28
 */
public class ColaBeanFactory extends DefaultListableBeanFactory{
    private static final Logger logger = LoggerFactory.getLogger(ColaBeanFactory.class);

    @Override
    protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final Object[] args)
        throws BeanCreationException {
        try {
            return super.doCreateBean(beanName, mbd, args);
        }catch (BeanCreationException e){
            throwKnowException(e);

            logger.error("doCreateBean error,will mock this Object!", e);
            Object proxy = newInstance(mbd, args);
            if (mbd == null || !mbd.isSynthetic()) {
                proxy = applyBeanPostProcessorsAfterInitialization(proxy, beanName);
            }
            return proxy;
        }
    }


    private Object newInstance(RootBeanDefinition mbd, Object... arg){
        Class clz = mbd.getTargetType();
        try {
            Class impClz = clz;
            Object oriTarget = Mockito.mock(impClz);
            return MockHelper.createMockFor(clz, new MockDataProxy(clz, oriTarget));
        }catch (Throwable e){
            return null;
        }
    }

    private boolean throwKnowException(BeanCreationException ex) throws BeanCreationException {
        if(!ex.getMessage().contains("java.lang.IllegalArgumentException: Could not resolve placeholder")){
            return true;
        }
        throw ex;
    }
}
