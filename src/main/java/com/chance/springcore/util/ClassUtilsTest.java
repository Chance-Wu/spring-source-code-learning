package com.chance.springcore.util;

import com.chance.dal.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * <p>
 * ClassUtils
 * 操作class类的方法
 * <p>
 *
 * @author chance
 * @since 2020-09-10
 */
public class ClassUtilsTest {

    @Test
    public void test() {
        Class<Person> clazz = Person.class;
        // 获取方法
        Method method = ClassUtils.getMethod(clazz, "getName");
        System.out.println(method);

        // 获取默认的类加载器
        ClassLoader defaultClassLoader = ClassUtils.getDefaultClassLoader();
        System.out.println(defaultClassLoader);
    }
}
