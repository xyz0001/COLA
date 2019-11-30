package com.alibaba.cola.mock.middleware;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * @author shawnzhan.zxy
 * @date 2019/06/25
 */
public class HsfFilter extends AbstractFilter{
    private static final String HSF = "HSF";
    private static final String DUBBO = "DUBBO";

    @Override
    public boolean doFilter(Object bean, BeanDefinition beanDefinition){
        //Object interfaceClassValue = beanDefinition.getPropertyValues().get("interfaceClass");
        Object groupValue = beanDefinition.getPropertyValues().get("group");
        //String interfaceClass = interfaceClassValue == null ? StringUtils.EMPTY : interfaceClassValue.toString();
        String group = groupValue == null ? StringUtils.EMPTY : groupValue.toString();
        if(HSF.equals(group) || DUBBO.equals(group)){
            return true;
        }else{
            return false;
        }
    }

    //private void parseElement(Field field) {
    //    HSFConsumer annotation = AnnotationUtils.getAnnotation(field, HSFConsumer.class);
    //    if (annotation == null) {
    //        return;
    //    }
    //
    //    HsfConsumerBeanDefinitionBuilder beanDefinitionBuilder = new HsfConsumerBeanDefinitionBuilder(field.getType(),
    //        annotation);
    //    beanDefinitionBuilder.context(context).beanFactory(beanFactory).properties(properties);
    //    BeanDefinition beanDefinition = beanDefinitionBuilder.build();
    //    beanDefinitions.put(field.getName(), beanDefinition);
    //}
}
