package com.example.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHelper implements ApplicationContextAware {

    public static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T popBean(Class<T> clazz){
        if (context == null){
            return null;
        }
        return context.getBean(clazz);
    }

    public static <T> T popBean(String name,Class<T> clazz){
        if (context == null){
            return null;
        }
        return context.getBean(name,clazz);
    }
}
