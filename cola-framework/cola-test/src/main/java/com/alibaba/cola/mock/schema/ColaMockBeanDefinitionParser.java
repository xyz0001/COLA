package com.alibaba.cola.mock.schema;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cola.mock.ColaMockController;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author shawnzhan.zxy
 * @date 2018/10/11
 */
public class ColaMockBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ColaMockController.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        String basePackages = element.getAttribute("base-package");
        bean.addConstructorArgValue(basePackages);
    }

    @Override
    protected boolean shouldGenerateId() {
        return true;
    }
}
