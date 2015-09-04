package com.weshaka.framework.beans;

import java.lang.reflect.Field;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.weshaka.ole.annotations.LoggingInfo;
import com.weshaka.ole.funcinf.Info;

/**
 * User: Alexis Hassler
 * 
 * @author ema
 */
@Component
public class LoggingAnnotationBeanPostProcessor implements MergedBeanDefinitionPostProcessor {
    public void postProcessMergedBeanDefinition(RootBeanDefinition rootBeanDefinition, Class<?> beanType, String beanName) {
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(LoggingInfo.class) != null) {
                injectInfoLogger(bean, field);
            }
        }
        return bean;
    }

    private void injectInfoLogger(Object bean, Field field) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, (Info)LoggerFactory.getLogger(field.getDeclaringClass())::info);
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
