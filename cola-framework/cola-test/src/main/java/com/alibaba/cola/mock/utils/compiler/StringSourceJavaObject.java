package com.alibaba.cola.mock.utils.compiler;

import java.net.URI;

import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

/**
 * @author shawnzhan.zxy
 * @date 2019/07/02
 */
public class StringSourceJavaObject extends SimpleJavaFileObject{
    final String code;

    public StringSourceJavaObject(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return this.code;
    }
}
