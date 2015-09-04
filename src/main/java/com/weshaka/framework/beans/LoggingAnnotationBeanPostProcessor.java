package com.weshaka.framework.beans;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.weshaka.ole.annotations.LoggingDebug;
import com.weshaka.ole.annotations.LoggingInfo;
import com.weshaka.ole.funcinf.LoggingPrinter;

/**
 * User: Alexis Hassler
 * 
 * @author ema
 */
@Component("loggingAnnotationBeanPostProcessor")
public class LoggingAnnotationBeanPostProcessor implements MergedBeanDefinitionPostProcessor {
    public void postProcessMergedBeanDefinition(RootBeanDefinition rootBeanDefinition, Class<?> beanType, String beanName) {
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> superClass = bean.getClass().getSuperclass();
        if(superClass!=null){
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotation(LoggingInfo.class) != null) {
                    injectInfoLogger(bean, field, (LoggingPrinter) LoggerFactory.getLogger(bean.getClass())::info);
                } else if (field.getAnnotation(LoggingDebug.class) != null) {
                    injectInfoLogger(bean, field, (LoggingPrinter) LoggerFactory.getLogger(bean.getClass())::debug);
                }
            }
        }
        return bean;
    }

    private void injectInfoLogger(Object bean, Field field, LoggingPrinter info) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, info);
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
