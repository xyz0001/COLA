package com.alibaba.cola.mock.utils;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cola.mock.model.ColaTestModel;

import static com.alibaba.cola.mock.utils.ClassUtils.convertClassFrom;
import static com.alibaba.cola.mock.utils.ClassUtils.getOriClassNameFromCGLIB;
import static com.alibaba.cola.mock.utils.Constants.COLAMOCK_PROXY_FLAG;

/**
 * 栈检索器
 * @author shawnzhan.zxy
 * @date 2019/05/22
 */
public class StackSearcher {

    /**
     * 是否第一个mock点
     * @param colaTestModel
     * @return
     */
    public static boolean isTopMockPoint(ColaTestModel colaTestModel, Class mockClass, String mockMethod){
        RuntimeException exception = new RuntimeException();
        try {
            StackTraceElement stackTraceElement = findTopMockPoint(colaTestModel, exception.getStackTrace());
            if(stackTraceElement == null){
                return false;
            }
            if(mockClass.isAssignableFrom(convertClassFrom(stackTraceElement.getClassName()))
                && mockMethod.equals(stackTraceElement.getMethodName())){
                return true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private static StackTraceElement findTopMockPoint(ColaTestModel colaTestModel, StackTraceElement[] stackTraceElements)
        throws ClassNotFoundException {
        for(int i = stackTraceElements.length - 1; i > 0; i--){
            StackTraceElement element = stackTraceElements[i];
            if(isConstantsExcludeStack(element)){
                continue;
            }
            if(colaTestModel.matchMockFilter(convertClassFrom(element.getClassName()), element.getMethodName())){
                return element;
            }
        }
        return null;
    }

    public static StackTraceElement[] getBusinessStack(ColaTestModel colaTestModel, StackTraceElement[] stackTraceElements){
        List<StackTraceElement> stacks = new ArrayList<>();
        for(StackTraceElement element : stackTraceElements){
            if(!isConstantsExcludeStack(element)
                && isMatchMockFilter(colaTestModel, element)){
                stacks.add(element);
            }
        }
        return stacks.toArray(new StackTraceElement[]{});
    }

    private static boolean isMatchMockFilter(ColaTestModel colaTestModel, StackTraceElement element){
        if(!element.getClassName().contains(COLAMOCK_PROXY_FLAG)){
            return true;
        }
        try {
            return colaTestModel.matchMockFilter(convertClassFrom(element.getClassName()), element.getMethodName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 需要忽略的栈点
     * @param element
     * @return
     */
    private static boolean isConstantsExcludeStack(StackTraceElement element){
        String className = element.getClassName();
        if(className.indexOf("com.alibaba.cola.") >= 0
            || className.indexOf("org.junit") >= 0
            || className.indexOf("org.springframework") >= 0
            || className.indexOf("java.") >= 0
            || className.indexOf("sun.") >= 0
            || className.indexOf("com.intellij") >= 0
            || className.indexOf("com.taobao.pandora.boot") >= 0
            || className.indexOf("org.mockito") >= 0
            ){
            return true;
        }
        //排除代理类
        if(className.contains("CGLIB$$") && !className.contains(COLAMOCK_PROXY_FLAG)){
            return true;
        }
        return false;
    }
}
