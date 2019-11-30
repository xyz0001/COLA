package com.alibaba.cola.mock.scan;

import com.alibaba.cola.mock.utils.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * mapper scanner
 * @author shawnzhan.zxy
 * @date 2018/09/24
 */
public class ClassScanner {
    private Environment environment = new StandardEnvironment();
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public List<Class> scanForClsAnnotation(Class<Annotation> annoCls, String... basePackages) throws Exception {
        List classList = new ArrayList<>();
        Set<Class<?>> candidates = findTestComponents(basePackages);
        for (Class<?> clazz : candidates){
            Annotation annotation = clazz.getAnnotation(annoCls);
            if(annotation != null){
                classList.add(clazz);
            }
        }
        return classList;
    }

    public List<Class> scanForSupper(Class supperClazz, String... basePackages) throws Exception {
        List classList = new ArrayList<>();
        Set<Class<?>> candidates = findTestComponents(basePackages);
        for (Class<?> clazz : candidates){
            if(supperClazz.isAssignableFrom(clazz)
                    && !Modifier.isAbstract(clazz.getModifiers())
                    && !Modifier.isInterface(clazz.getModifiers())) {
                classList.add(clazz);
            }
        }
        return classList;
    }

    private Set<Class<?>> findTestComponents(String... basePackages) throws Exception {
        Set<Class<?>> testClzzList = new HashSet<>();
        for(String basePackage : basePackages){
            if(StringUtils.isBlank(basePackage)){
                continue;
            }
            String resourcePath = ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));

            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resourcePath + '/' + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            for(Resource res : resources){
                String className = ClassUtils.convertResourcePathToClassName(res.getInputStream());
                Class testClass = Thread.currentThread().getContextClassLoader().loadClass(className);
                testClzzList.add(testClass);
            }
        }

        return testClzzList;
    }
}
