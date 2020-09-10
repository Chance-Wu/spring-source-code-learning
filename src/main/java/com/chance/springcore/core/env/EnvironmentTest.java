package com.chance.springcore.core.env;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>
 *
 * <p>
 *
 * @author chance
 * @since 2020-09-09
 */
public class EnvironmentTest {

    public static void main(String[] args) {
        // 继承了Environment
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
        // ClassPathXmlApplicationContext实例对象的getEnvironment方法获取环境
        ConfigurableEnvironment environment = context.getEnvironment();
        environment.getProperty("name");
    }
}
