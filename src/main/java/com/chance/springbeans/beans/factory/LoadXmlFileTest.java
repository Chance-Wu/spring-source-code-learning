package com.chance.springbeans.beans.factory;

import com.chance.dal.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * <p>
 * spring中加载配置文件
 * <p>
 *
 * @author chance
 * @since 2020-09-10
 */
public class LoadXmlFileTest {

    @Test
    public void loadXmlFileTest() {
        // spring 4.0之前使用XmlBeanFactory加载配置文件
        // 1.将spring的配置文件抽象成一个Resource对象
        ClassPathResource resource = new ClassPathResource("spring-config.xml");
        // 2.定义bean工厂，XmlBeanFactory内维护了一个XMLBeanDefinitionReader对象，
        XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
        Person person1 = (Person) beanFactory.getBean("person");

        // spring4.0之后的采用这种新版的加载方式
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        Person person2 = (Person) context.getBean("person");
    }
}
