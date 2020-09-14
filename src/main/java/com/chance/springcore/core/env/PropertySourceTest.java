package com.chance.springcore.core.env;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;

/**
 * <p>
 *
 * <p>
 *
 * @author chance
 * @since 2020-09-09
 */
public class PropertySourceTest {

    @Test
    public void test() {
        /*Properties properties = new Properties();
        properties.put("name", "chance");
        PropertiesPropertySource source = new PropertiesPropertySource("name", properties);

        // 属性源，key-value属性对，比如用于配置数据。
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(source);

        // 属性解析器，用于解析相应的key的value
        PropertySourcesPropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);
        String value = propertyResolver.getProperty("name");
        System.out.println(value);*/

        HashMap<String, Object> map = new HashMap<>(16);
        map.put("name","chance");
        map.put("age",18);
        map.put("sex","man");
        // 属性来自于一个Map
        MapPropertySource mapPropertySource = new MapPropertySource("properties", map);

//        ResourcePropertySource resourcePropertySource = new ResourcePropertySource(...);


//        ServletContextPropertySource servletContextPropertySource = new ServletContextPropertySource(...);
    }


}
