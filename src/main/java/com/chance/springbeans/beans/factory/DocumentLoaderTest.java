package com.chance.springbeans.beans.factory;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 *
 * <p>
 *
 * @author chance
 * @since 2020-09-12
 */
@Slf4j
public class DocumentLoaderTest {

    @Test
    public void testDocumentLoader() throws Exception {
        // 设置资源
        EncodedResource encodedResource = new EncodedResource(new ClassPathResource("spring-config.xml"));

        // 加载解析
        InputSource inputSource = new InputSource(encodedResource.getResource().getInputStream());
        ResourceEntityResolver entityResolver = new ResourceEntityResolver(new PathMatchingResourcePatternResolver());
        DefaultHandler defaultHandler = new DefaultHandler();

        DefaultDocumentLoader documentLoader = new DefaultDocumentLoader();
        Document doc = documentLoader.loadDocument(inputSource, entityResolver, defaultHandler, XmlValidationModeDetector.VALIDATION_XSD, false);

        // 输出结果
        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (!(node instanceof Element)) {
                continue;
            }
            Element ele = (Element) node;
            if (!"bean".equals(ele.getNodeName())) {
                continue;
            }
            String id = ele.getAttribute("id");
            String clazz = ele.getAttribute("class");
            String scope = ele.getAttribute("scope");
            log.info("测试结果 \n" +
                    "beanName：{} \n" +
                    "beanClass：{} \n" +
                    "scope：{}", id, clazz, scope);
        }
    }
}
