package com.chance.springbeans.beans.factory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 演示IoC容器的创建过程
 * <p>
 *
 * @author chance
 * @since 2020-09-11
 */
public class IocCreationProcessTest {

    @Test
    public void iocCreationProcessTest() {
        // 1.创建IoC配置文件的抽象资源
        ClassPathResource resource = new ClassPathResource("spring-config.xml");
        // 2.创建一个Bean工厂
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 3.把读取配置信息的BeanDefinitionReader,这里是XmlBeanDefinitionReader配置给BeanFactory
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        // 4.从定义好的资源位置读入配置信息，具体的解析过程由XmlBeanDefinitionReader来完成
        reader.loadBeanDefinitions(resource);

        System.out.println(beanFactory.getBean("person"));
    }
}
