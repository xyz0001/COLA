package com.alibaba.cola.mock.scan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.cola.mock.utils.Constants;

/**
 * @author shawnzhan.zxy
 * @date 2019/01/10
 */
public class AnnotationScanner {
    private final Class<?> ownerClazz;
    private final Class annotation;
    private Map<Field, Annotation> fieldMap = new HashMap<>();

    public AnnotationScanner(Class<?> ownerClazz, Class annotation) {
        this.ownerClazz = ownerClazz;
        this.annotation = annotation;
    }

    public void addTo(Set<Field> mockDependentFields) {
        mockDependentFields.addAll(scan().keySet());
    }

    public Map<Field, Annotation> scan() {
        Set<Field> mockDependentFields = new HashSet<Field>();
        Field[] fields = ownerClazz.getDeclaredFields();
        for (Field field : fields) {
            Annotation ann = field.getAnnotation(annotation);
            if (null != ann) {
                mockDependentFields.add(field);
                fieldMap.put(field, ann);
            }
        }

        return fieldMap;
    }
}
