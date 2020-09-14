package com.chance.springcore.core;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p>
 * org.springframework.core.annotation.AnnotationUtils
 * <p>
 * 注解解析器和工具类
 * <p>
 *
 * @author chance
 * @since 2020-09-09
 */
public class AnnotationUtilTest {

    @Test
    @Hello("hello")
    public void test() throws Exception {
        // 获取Class对象
        Class<?> clazz = Class.forName("com.chance.springcore.core.AnnotationUtilTest");
        // 通过Class对象的 getMethod(方法名,参数)方法 得到main方法对象
        Method method = clazz.getMethod("test");
        // 获取方法上的注解
        Hello methodAnnotation = method.getAnnotation(Hello.class);

        // 使用spring框架里的注解工具类得到注解属性
        Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(methodAnnotation);
        Object value = AnnotationUtils.getValue(methodAnnotation, "value");

        System.out.println(annotationAttributes);
        System.out.println(value);
    }
}
