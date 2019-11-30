package com.alibaba.cola.mock.model;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.cola.mock.ColaMockito;
import com.alibaba.cola.mock.annotation.ColaMock;
import com.alibaba.cola.mock.annotation.ColaMockConfig;
import com.alibaba.cola.mock.annotation.ExcludeCompare;
import com.alibaba.cola.mock.scan.AnnotationScanner;
import com.alibaba.cola.mock.scan.RegexPatternTypeFilter;
import com.alibaba.cola.mock.scan.TypeFilter;
import com.alibaba.cola.mock.utils.ClassUtils;
import com.alibaba.cola.mock.utils.FileUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author shawnzhan.zxy
 * @date 2018/09/24
 * 测试类的配置元数据
 */
public class ColaTestModel implements Serializable{
    Class<?> testClazz;
    transient ColaMockConfig colaMockConfig;
    List<TypeFilter> mockFilters = new ArrayList<>();
    /** 类方法拦截配置*/
    Map<Class, List<RegexPatternTypeFilter>> methodFilterMap = new HashMap<Class, List<RegexPatternTypeFilter>>();
    List<TypeFilter> dataManufactureFilters = new ArrayList<>();
    Map<String, Object> dataMaps;
    /**
     * 单测方法，单测方法配置
     */
    Map<String, ExcludeCompare> noNeedCompareConfigMap = new HashMap<String, ExcludeCompare>();

    public void putCurrentTestMethodConfig(String testMethodName, ExcludeCompare currentMethodConfig){
        noNeedCompareConfigMap.put(testMethodName,currentMethodConfig);
    }

    public void putMethodFilter(Class fieldType, List<RegexPatternTypeFilter> filterList){
        methodFilterMap.put(fieldType, filterList);
    }

    public ExcludeCompare getCurrentTestMethodConfig(String testMethodName){
        return noNeedCompareConfigMap.get(testMethodName);
    }

    public Map<String, ExcludeCompare> getNoNeedCompareConfigMap() {
        return noNeedCompareConfigMap;
    }

    public Class<?> getTestClazz() {
        return testClazz;
    }

    public void setTestClazz(Class<?> testClazz) {
        this.testClazz = testClazz;
    }

    public ColaMockConfig getColaMockConfig() {
        return colaMockConfig;
    }

    public void setColaMockConfig(ColaMockConfig colaMockConfig) {
        this.colaMockConfig = colaMockConfig;
    }

    public <T> T getDataMap(String key){
        if(dataMaps == null){
            dataMaps = ColaMockito.g().loadDataMaps(FileUtils.getAbbrOfClassName(testClazz.getName()));
        }
        return (T)this.dataMaps.get(key);
    }

    /**
     * 获取dataMap
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getDataMap4Strict(String key){
        T result = getDataMap(key);
        if(result == null && !this.dataMaps.containsKey(key)){
            throw new RuntimeException(String.format("%s dataMap not exists!", key));
        }
        return result;
    }

    public void saveDataMap(String key, Object value){
        if(dataMaps == null){
            dataMaps = ColaMockito.g().loadDataMaps(testClazz.getName());
        }
        dataMaps.put(key, value);
        ColaMockito.g().getDataMapStore().save(dataMaps, testClazz.getName());
    }

    public void addMockFilter(TypeFilter filter) {
        this.mockFilters.add(filter);
    }

    public void addDataManufactureFilter(TypeFilter filter) {
        this.dataManufactureFilters.add(filter);
    }

    public List<TypeFilter> getTypeFilters() {
        return mockFilters;
    }

    public boolean matchMockFilter(Class clazz){
        return matchMockFilter(clazz, null);
        //for(TypeFilter filter : mockFilters){
        //    if(filter.match(clazz)){
        //        return true;
        //    }
        //}
        //return false;
    }

    public boolean matchMockFilter(Class clazz, String methodName){
        if(matchClass(clazz)){
            return matchMethod(clazz, methodName);
        }
        return false;
    }

    public boolean matchDataManufactureFilter(Class clazz){
        for(TypeFilter filter : dataManufactureFilters){
            if(filter.match(clazz)){
                return true;
            }
        }
        return false;
    }

    public void reScanConfigForInstance(){
        Map<Field, Annotation> fieldAnnotationMap = new AnnotationScanner(testClazz, ColaMock.class).scan();
        Set<Entry<Field, Annotation>> set = fieldAnnotationMap.entrySet();
        Iterator<Entry<Field, Annotation>> it = set.iterator();
        while (it.hasNext()){
            Entry<Field, Annotation> fieldEntry = it.next();
            ColaMock colaMock = (ColaMock)fieldEntry.getValue();
            List<RegexPatternTypeFilter> regexFilterList = new ArrayList<>();
            for(String methodName : colaMock.methods()){
                regexFilterList.add(new RegexPatternTypeFilter(methodName));
            }
            putMethodFilter(fieldEntry.getKey().getType(),
                regexFilterList);
        }
    }

    private boolean matchClass(Class clazz){
        for(TypeFilter filter : mockFilters){
            if(filter.match(clazz)){
                return true;
            }
        }
        return false;
    }

    private boolean matchMethod(Class clazz, String methodName){
        boolean match = true;
        if(StringUtils.isBlank(methodName)){
            return match;
        }
        Set<Entry<Class, List<RegexPatternTypeFilter>>> set = methodFilterMap.entrySet();
        Iterator<Entry<Class, List<RegexPatternTypeFilter>>> it = set.iterator();
        while (it.hasNext()){
            Entry<Class, List<RegexPatternTypeFilter>> entry = it.next();
            if(ClassUtils.isAssignableWithUnionAll(entry.getKey(), clazz)){
                for(RegexPatternTypeFilter filter : entry.getValue()){
                    return filter.match(methodName);
                }
            }
        }
        return match;
    }
}
