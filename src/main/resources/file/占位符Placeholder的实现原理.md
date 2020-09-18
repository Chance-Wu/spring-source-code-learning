### 1、回顾在Spring中如何使用占位符

application-dev.yml

```yaml
jwt:
  expire: 3600
```

### 2、~~PropertyPlaceConfigure~~ 类解析

spring实现占位符替换主要涉及到  **BeanFactoryPostProcessor接口** 和 **PropertyPlaceholderConfigurer** 、 **PlaceholderConfigurerSupport** 、 **PropertyResourceConfigurer** 三个类。![](../../../../../../Pictures/assets/007S8ZIlgy1gitqfxqyirj31x80lkn0a.jpg)

#### 2.1 BeanFactoryPostProcessor 接口

spring提供了 **BeanFactoryPostProcessor的容器扩展机制** 。<u>它允许我们在容器实例化对象之前，对容器中的BeanDefinition中的信息做一定的修改</u>（比如对某些字段的值进行修改，这就是占位符替换的根本）。

```java
/**
 * Factory hook，允许自定义修改应用程序上下文的bean定义，调整上下文基础的bean属性值bean工厂。
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

	/**
	 * 在标准初始化之后，修改应用程序上下文的内部bean工厂。 
	 * 所有bean定义都将被加载，但尚未实例化任何bean。 
	 * 这允许覆盖或添加属性，甚至可以初始化Bean。
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
```

#### 2.2 PreopertyPlaceholderConfigurer 类

只要向Spring容器注入了 **BeanFactoryPostProcessor接口** 的实现类，那么在容器实例化对象之前会先调用这个类的 `postProcessorBeanFactory()方法 `，而 **PreopertyPlaceholderConfigurer类** 正是间接实现了 **BeanFactoryPostProcessor接口** 才完成了占位符的实现。

`postProcessorBeanFactory(ConfigurableListableBeanFactory beanFactory)`方法的参数是 **ConfigurableListableBeanFactory类** 的一个对象，实际开发中，常使用它的 `getBeanDefinition()方法` 获取某个bean的元数据定义：BeanDefinition。

真正来实现 **BeanFactoryPostProcessor接口** 的是 **PropertyResourceConfigurer类** ，来看看它是如何实现 `postProcessBeanFactory方法` 的。

```java

@Override
public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    try {
        // 1.把加载的所有的properties文件中的键值对都取出来保存在一起
        Properties mergedProps = mergeProperties();

        // 2.如有必要，请转换合并的属性
        convertProperties(mergedProps);

        // 3.让子类处理属性
        processProperties(beanFactory, mergedProps);
    }
    catch (IOException ex) {
        throw new BeanInitializationException("Could not load properties", ex);
    }
}
```

1. 容器首先会走到这里的 `postProcessBeanFactory方法` 里面，主要看 `processProperties方法` ，这个方法是抽象的，具体在 **PropertyPlaceholderConfigurer类** 中实现，如下代码：

```java
/**
	* 访问给定bean工厂中的每个bean定义，并尝试替换$ {...}属性具有给定属性值的占位符。
	*/
@Override
protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
    throws BeansException {
	
    StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
    doProcessProperties(beanFactoryToProcess, valueResolver);
}
```

2. 首先是实例化一个 `PlaceholderResolvingStringValueResolver` 对象，先看 **PlaceholderResolvingStringValueResolver类** ，这个是 **PropertyPlaceholderConfigurer** 的内部类，代码如下：

```java
private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

    private final PropertyPlaceholderHelper helper;

    private final PlaceholderResolver resolver;

    public PlaceholderResolvingStringValueResolver(Properties props) {
        this.helper = new PropertyPlaceholderHelper(
        placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
        this.resolver = new PropertyPlaceholderConfigurerResolver(props);
    }
 
    @Override
    @Nullable
    public String resolveStringValue(String strVal) throws BeansException {
        String resolved = this.helper.replacePlaceholders(strVal, this.resolver);
        if (trimValues) {
            resolved = resolved.trim();
        }
        return (resolved.equals(nullValue) ? null : resolved);
    }
}
```

3. 可以看到这个类实现了 **StringValueResolver接口** ，这个类的作用是我们分析到后面再回头看。这个对象作为参数传给了 **doProcessProperties方法** ，这个方法是在父类 **PlaceholderConfigurerSupport** 中实现的，代码如下：

```java
protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
			StringValueResolver valueResolver) {

    BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

    String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
    for (String curName : beanNames) {
        if (!(curName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
            BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
            try {
                visitor.visitBeanDefinition(bd);
            }
            catch (Exception ex) {
                throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
            }
        }
    }

    // 解析别名目标名称和别名中的占位符
    beanFactoryToProcess.resolveAliases(valueResolver);

    // 解决嵌入值（例如注释属性）中的占位符
    beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
}
```

4. 首先根据传进来的 `valueResolver` 对象生成了一个 `BeanDefinitionVisitor` 对象，然后循环遍历beanFactory里面的每一个bean，获取它们的BeanDefinition，然后调用了 `visitor.visitBeanDefinition(db)` ，所以重点就是BeanDefinitionVisitor这个对象的visitBeanDefinition()方法到底对我们的BeanDefinition做了什么事情，go on，截取了 **BeanDefinitionVisitor** 这个类的比较重要的部分代码：

```java
public class BeanDefinitionVisitor {

	@Nullable
	private StringValueResolver valueResolver;


	/**
	 * BeanDefinitionVisitor 构造器，将指定的值解析器应用于所有bean元数据值。
	 */
	public BeanDefinitionVisitor(StringValueResolver valueResolver) {
		Assert.notNull(valueResolver, "StringValueResolver must not be null");
		this.valueResolver = valueResolver;
	}

	/**
	 * 遍历给定的BeanDefinition对象以及其中包含的MutablePropertyValues和ConstructorArgumentValues。
	 */
	public void visitBeanDefinition(BeanDefinition beanDefinition) {
		visitParentName(beanDefinition);
		visitBeanClassName(beanDefinition);
		visitFactoryBeanName(beanDefinition);
		visitFactoryMethodName(beanDefinition);
		visitScope(beanDefinition);
		if (beanDefinition.hasPropertyValues()) {
      	 // 重点，调用visitPropertyValues
			visitPropertyValues(beanDefinition.getPropertyValues());
		}
		if (beanDefinition.hasConstructorArgumentValues()) {
			ConstructorArgumentValues cas = beanDefinition.getConstructorArgumentValues();
			visitIndexedArgumentValues(cas.getIndexedArgumentValues());
			visitGenericArgumentValues(cas.getGenericArgumentValues());
		}
	}
  
   protected void visitPropertyValues(MutablePropertyValues pvs) {
		PropertyValue[] pvArray = pvs.getPropertyValues();
		for (PropertyValue pv : pvArray) {
      	 // 调用resolveVaue
			Object newVal = resolveValue(pv.getValue());
			if (!ObjectUtils.nullSafeEquals(newVal, pv.getValue())) {
				pvs.add(pv.getName(), newVal);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Nullable
	protected Object resolveValue(@Nullable Object value) {
		// 省略部分代码
       if (value instanceof String) {
			return resolveStringValue((String) value);
		}
		return value;
	}

	/**
	 * 解决给定的String值，例如解析占位符。
	 */
	@Nullable
	protected String resolveStringValue(String strVal) {
		// 省略部分代码
		String resolvedValue = this.valueResolver.resolveStringValue(strVal);
		// 如果未修改，则返回原始String。
		return (strVal.equals(resolvedValue) ? strVal : resolvedValue);
	}

}
```

5. 首先看到构造方法，接收了一个 `StringValueResolver` 接口实现类实例作为成员变量，最终在 `resolveStringValue` 这个方法中调用 `this.valueResolver.resolveStringValue(strVal)` 发挥作用，来看看这个很关键的 `visitBeanDefinition方法` ，`visitPropertyValues(beanDefinition.getPropertyValues());`这一行中`beanDefinition.getPropertyValues()`取出了这个 bean 的所有属性，在这里我们看到了我们熟悉的占位符，

![](../../../../../../Pictures/assets/007S8ZIlgy1giujm5mcrwj30xc0nt767.jpg)

6. 在**visitPropertyValues**调用了**resolveValue**去取这个属性的值对应的最终的结果，因为占位符是 string 的形式，所以最后会落到**resolveStringValue**方法中，在这个方法中我们可以看到`String resolvedValue = this.valueResolver.resolveStringValue(strVal);`，这里的 **strVal** 就是图中看到的`${mysql.druid.initialSize}`，而`this.valueResolver`正是构造函数传进来的参数，也就是**PlaceholderConfigurerSupport**调用`BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);`作为参数传过来的的**valueResolver**，这个对象是最开始提到的**PlaceholderResolvingStringValueResolver**这个类，所以应该就是这个类的**resolveStringValue**方法把`${mysql.druid.initialSize}`替换成 properties 文件中对应的值的。

7. 现在重点是PlaceholderResolvingStringValueResolver这个类，该类是PropertyPlaceholderConfigurer的内部类，代码如下：

```java
private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

    private final PropertyPlaceholderHelper helper;

    private final PlaceholderResolver resolver;

    public PlaceholderResolvingStringValueResolver(Properties props) {
        this.helper = new PropertyPlaceholderHelper(
        placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
        this.resolver = new PropertyPlaceholderConfigurerResolver(props);
    }

    @Override
    @Nullable
    public String resolveStringValue(String strVal) throws BeansException {
        String resolved = this.helper.replacePlaceholders(strVal, this.resolver);
        if (trimValues) {
            resolved = resolved.trim();
        }
        return (resolved.equals(nullValue) ? null : resolved);
    }
}
```

通过代码可以看到，占位符的替换主要就是在**PropertyPlaceholderHelper**类的**replacePlaceholders**方法中实现的，这个方法代码有点长就不粘贴出来了，感兴趣的可以自己看看**PropertyPlaceholderHelper**源码，也比较简单，总结起来就是找到参数**strVal**中被`#{`和`}`包含的部分，然后调用传入的**resolver**的**resolvePlaceholder**方法找到对应的值来进行替换（如果有嵌套的会递归替换），来看看这个**PlaceholderResolver**接口的实现类**PropertyPlaceholderConfigurerResolver**。

8. **PlaceholderResolver**接口是在**PropertyPlaceholderHelper**类中定义的一个内部类接口，**PropertyPlaceholderConfigurer**中定义了内部类**PropertyPlaceholderConfigurerResolver**实现了这个接口，代码如下：

```java
private final class PropertyPlaceholderConfigurerResolver implements PlaceholderResolver {

	private final Properties props;

	private PropertyPlaceholderConfigurerResolver(Properties props) {
		this.props = props;
	}

	@Override
	@Nullable
	public String resolvePlaceholder(String placeholderName) {
		return PropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName,
				this.props, systemPropertiesMode);
	}
}
```

resolverPlaceholder实现的逻辑如下，说白了就是去Properties对象中去对应的值，这你的props就是最开始根据properties文件生成的，所以到这里应该就真相大白了。

```java
/**
  * This implementation tries to resolve placeholders as keys first
  * in the user preferences, then in the system preferences, then in
  * the passed-in properties.
  */
@Override
protected String resolvePlaceholder(String placeholder, Properties props) {
    String path = null;
    String key = placeholder;
    int endOfPath = placeholder.lastIndexOf('/');
    if (endOfPath != -1) {
        path = placeholder.substring(0, endOfPath);
        key = placeholder.substring(endOfPath + 1);
    }
    String value = resolvePlaceholder(path, key, this.userPrefs);
    if (value == null) {
        value = resolvePlaceholder(path, key, this.systemPrefs);
        if (value == null) {
            value = props.getProperty(placeholder);
        }
    }
    return value;
}
```



