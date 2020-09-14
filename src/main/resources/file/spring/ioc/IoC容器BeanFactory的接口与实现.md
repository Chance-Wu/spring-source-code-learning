spring-core核心包中，不仅定义了IoC容器的最基本接口（BeanFactory），也提供了一系列这个接口的实现。

> 设计理念：Spring将所有的东西都当成是`Resource`。外界只传递Resource对象就可以了，`Spring通过beanFactory来加载配置、管理对象`

1、演示IoC容器的创建过程
--
1. 定义好spring的配置文件（如.xml）
2. 通过Resource对象将spring的配置文件进行抽象，抽象成一个Resource对象
3. 定义好bean工厂
4. 定义好XMLBeanDefinitionReader对象，并将工厂作为参数传递进去供后续回调使用
5. 通过XMLBeanDefinitionReader对象读取之前抽象出的Resource对象（包含了xml文件解析过程）
6. 本质上，xml文件的解析是由XmlBeanDefinitionReader对象交由BeanDefinitionParserDelegate委托来完成的，实际上这里使用了委托模式
7. IoC容器创建完毕，可以通过容器获得所有对象的信息

```text
// 1.创建IoC配置文件的抽象资源
ClassPathResource resource = new ClassPathResource("factoryBean.xml");
// 2.创建一个Bean工厂
DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
// 3.把读取配置信息的BeanDefinitionReader,这里是XmlBeanDefinitionReader配置给BeanFactory
XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
// 4.从定义好的资源位置读入配置信息，具体的解析过程由XmlBeanDefinitionReader来完成
reader.loadBeanDefinitions(resource);
```

2、XmlBeanFactory（已过期）
--
如XmlBeanFactory就是一个最基本的BeanFactory（IoC容器），它能够支持通过XML文件配置的Bean定义信息。除此之外，Spring IoC容器还提供了一个容器系列，如SimpleJndiBeanFactory、StaticListableBeanFactory等。

源码：
```java
@Deprecated
@SuppressWarnings({"serial", "all"})
public class XmlBeanFactory extends DefaultListableBeanFactory {

    /**定义一个默认使用的bean定义读取器，完成对xml的读取*/
	private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);


	/**
	 * 使用给定的资源创建一个新的XmlBeanFactory，必须可以被DOM解析。
	 * 
	 * 在加载或解析错误的情况下抛出BeansException。
	 */
	public XmlBeanFactory(Resource resource) throws BeansException {
		this(resource, null);
	}

	/**
	 * 使用给定的输入流创建一个新的XmlBeanFactory，必须可以被DOM解析。
	 * @param resource xml资源
	 * @param parentBeanFactory 父bean工厂
	 * 在加载或解析错误的情况下抛出BeansException
	 */
	public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
		super(parentBeanFactory);
		this.reader.loadBeanDefinitions(resource);
	}
}
```

> 读取器读取资源和注册bean定义信息的整个过程，基本上是和上下文的处理是一样的，从这里可看到上下文和XmlBeanFactory这两种IOC容器的区别:
* BeanFactory往往不具备对资源定义的能力，
* 而上下文可自己完成资源定义，从这个角度上看上下文更好用一些。 

1. 通过Resource 接口来抽象bean定义数据。（对Xml定义文件的解析通过`委托给XmlBeanDefinitionReader`来完成）
2. XmlBeanFactory（Spring3.1之前使用）（用xml来定义IOC容器中的bean）: 通过使用模板模式来得到对IOC 容器的抽象- AbstractBeanFactory,DefaultListableBeanFactory 这些抽象类为其提供模板服务。
3. XmlBeanFactory内维护了一个XMLBeanDefinitionReader对象，在XmlBeanFactory构造的时候，将XmlBeanFactory对象注入保存为BeanDefinitionRegistry（XmlBeanDefinitionReader中）
4. 通过XmlBeanDefinitionReader对象调用loadBeanDefinitions读取之前抽象出的Resources对象（包含了xml文件解析过程），xml文件的解析是由XmlBeanDefinitionReader对象交由BeanDefinitionParserDelegate委派来完成（委派模式）
   * XmlBeanDefinitionReader内部维护了一个DocumentLoader还有ThreadLocal等
   * loadBeanDefinitions方法将先从ThreadLocal中获取资源容器set。如果没有则创建一个四空间大小的。然后set进去。如果有了，就把当前的资源文件add到这个set容器中。将这个资源文件转为字节流（如果编码了会进行转码），然后调用doLoadBeanDefinitions方法进行加载配置。
   * doLoadBeanDefinitions方法将先调用doLoadDocument对传入的资源字节流进行转换为Document。最后调用registerBeanDefinitions进行注册，返回注册的bean的数量。
        * doLoadDocument方法是使用DefaultDocumentLoader的loadDocument方法，最终返回一个Document
        * registerBeanDefinitions则是使用DefaultNamespaceHandlerResolver进行解析Document，调用其registerBeanDefinitions进行注入。
        * DefaultNamespaceHandlerResolver 的registerBeanDefinitions方法中先是获取了xml的根元素，然后根据根元素调用doRegisterBeanDefinitions方法去逐个获取元素。
        * doRegisterBeanDefinitions方法委托了BeanDefinitionParserDelegate类进行解析。调用了parseBeanDefinitions方法来解析元素。【委托模式可以实现业务的解耦】
        * parseBeanDefinitions方法中，循环进行遍历，调用parseDefaultElement进行比对，如果是对应的配置，如bean，则调用相应的方法，比方说bean的就调用了processBeanDefinition方法
        * processBeanDefinition方法中最终调用了BeanDefinitionReaderUtils.registerBeanDefinition。这里会最终调用到BeanDefinitionRegistry，也就是当前的XmlBeanFactory的registerBeanDefinition方法，这个方法主要就是put元素进到内部维护的map中（在XmlBeanFactory的父类中实现，也就是DefaultListableBeanFactory中实现。同理，这里实现了其他的注册，都是以map的方式进行维护的）
5. 委派得到的结果（bean等），通过调用XmlBeanFactory的父类DefaultListableBeanFactory的registerBeanDefinition方法，put到map中进行维护。IOC、容器创建完毕，可以通过容器获得所有对象的信息。

```text
// 读取xml文件，解析文件，将元素中的东西封装为对象。被spring管理
ClassPathResource resource = new ClassPathResource("beanFactory.xml");
XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
Person person1 = (Person) beanFactory.getBean("person");
```

3、ClassPathXmlApplicationContext（推荐采用这种加载方式）
--
```text
ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beanFactory.xml");
Person person2 = (Person) context.getBean("person");
```






































XmlBeanDefinitionReader 源码
--
























