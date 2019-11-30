package com.alibaba.cola.mock.agent.logger;

import com.alibaba.cola.mock.ColaMockito;

public class LoggerFactory {

    public static Logger getLogger(Class<?> clazz) {
        org.slf4j.Logger slfjLogger = org.slf4j.LoggerFactory.getLogger(clazz);
        return new SLFJLogger(slfjLogger);
    }

    public static Logger getLogger(String loggerName) {
        org.slf4j.Logger slfjLogger = org.slf4j.LoggerFactory.getLogger(loggerName);
        return new SLFJLogger(slfjLogger);
    }
}
