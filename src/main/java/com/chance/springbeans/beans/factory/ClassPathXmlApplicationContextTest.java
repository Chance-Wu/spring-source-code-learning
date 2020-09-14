package com.chance.springbeans.beans.factory;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p>
 *
 * <p>
 *
 * @author chance
 * @since 2020-09-12
 */
public class ClassPathXmlApplicationContextTest {

    @Test
    public void testClassPathXmlApplicationContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");

        System.out.println(context.getBean("person"));
    }
}
