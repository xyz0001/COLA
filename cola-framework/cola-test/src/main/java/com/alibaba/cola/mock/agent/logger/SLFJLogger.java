package com.alibaba.cola.mock.agent.logger;

import com.alibaba.cola.mock.ColaMockito;

public class SLFJLogger implements Logger{
    private org.slf4j.Logger slfjLogger;
    
    public SLFJLogger(org.slf4j.Logger slfjLogger) {
        this.slfjLogger = slfjLogger;
    }

    @Override
    public void info(String msg) {
        if(ColaMockito.debugModel) {
            slfjLogger.info(msg);
        }
    }

    @Override
    public void error(String msg) {
        if(ColaMockito.debugModel) {
            slfjLogger.error(msg);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if(ColaMockito.debugModel) {
            slfjLogger.error(msg, t);
        }
    }

}
