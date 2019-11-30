package com.alibaba.cola.mock.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.alibaba.cola.mock.utils.compiler.StringSourceJavaObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.asm.ClassReader;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.Assert;

import static com.alibaba.cola.mock.utils.Constants.DOT;
import static com.alibaba.cola.mock.utils.Constants.PATH_SEPARATOR;

/**
 * @author shawnzhan.zxy
 * @date 2019/07/01
 */
public class ClassUtils {

    public static Class compileAndLoadClass(String javaFile){
        String classFile = compile(javaFile);
        try {
            return loadClassWithException(convertResourcePathToClassName(classFile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertResourcePathToClassName(InputStream is) throws IOException {
        ClassReader classReader = new ClassReader(is);
        String className = classReader.getClassName();
        className = className.replaceAll("/", ".");
        return className;
    }

    public static String convertResourcePathToClassName(String classFile) throws IOException {
        return convertResourcePathToClassName(new FileInputStream(classFile));
    }

    public static String convertClassNameToResourcePath(String className) {
        Assert.notNull(className, "Class name must not be null");
        return className.replace(DOT, PATH_SEPARATOR);
    }

    public static String compile(String javaFile){
        String classFile = StringUtils.EMPTY;
        //动态编译
        String content = FileUtils.readFile(javaFile);

        StringSourceJavaObject sourceObject = new StringSourceJavaObject("Main", content);
        Iterable<JavaFileObject> fileObjects = Arrays.asList(sourceObject);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager =compiler.getStandardFileManager(null,null,null);

        String classPath = FileUtils.getDefaultTestClassDirectory();
        Iterable<String> options = Arrays.asList("-d", classPath,"-cp",classPath);

        CompilationTask task=compiler.getTask(null, fileManager,null,options,null,fileObjects);
        //int status = javac.run(null, null, null, "-d", FileUtils.getDefaultTestClassDirectory(), javaFile);
        boolean result=task.call();
        if(result){
            classFile = javaFile.replace(FileUtils.getDefaultDirectory4TestFile(), FileUtils.getDefaultTestClassDirectory());
            classFile = classFile.replace(".java", ".class");
        }
        return classFile;
    }

    public static Class loadClassWithException(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    public static Class loadClass(String className){
        try {
            return loadClassWithException(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class convertClassFrom(String className) throws ClassNotFoundException {
        className = getOriClassNameFromCGLIB(className);
        return Class.forName(className);
    }

    public static String getOriClassNameFromCGLIB(String cglibClassName){
        String[] classNamePS = cglibClassName.split("\\$\\$");
        return classNamePS[0];
    }

    public static String getClassName(Object bean, BeanDefinition beanDefinition){
        String className = null;
        if(bean instanceof FactoryBean){
            className = ((FactoryBean)bean).getObjectType().getName();
        }else if(beanDefinition instanceof AnnotatedBeanDefinition){
            MethodMetadata methodMetadata = ((AnnotatedBeanDefinition)beanDefinition).getFactoryMethodMetadata();

            if(methodMetadata != null){
                className = methodMetadata.getReturnTypeName();
            }else{
                className = ((AnnotatedBeanDefinition)beanDefinition).getMetadata().getClassName();
            }
        }else if(beanDefinition instanceof RootBeanDefinition){
            className = bean.getClass().getName();
        }else if(bean instanceof Proxy ||
            bean.getClass().getName().indexOf(Constants.COLAMOCK_PROXY_FLAG) > -1){
            className = getClassNameFromProxy(bean);
        }else{
            className = beanDefinition.getBeanClassName();
        }
        return className;
    }

    public static boolean isAssignableWithUnionAll(Class s1, Class s2){
        return s1.isAssignableFrom(s2) || s2.isAssignableFrom(s1);
    }

    private static String getClassNameFromProxy(Object proxy){
        Class[] intfs = proxy.getClass().getInterfaces();
        if(intfs != null && intfs.length > 0){
            return intfs[0].getName();
        }
        return proxy.toString();
    }
}
