BeanDefinitionParserDelegate 源码（Spring真正的解析代理类）
--
有状态的委托类，用于解析XML bean定义。

BeanDefinitionParserDelegate类提供了解析spring配置文件功能：
* 对于默认空间下的元素()在该类内部实现，
* 对于其它命名空间下的元素可以通过绑定NamespaceHandler的方式来实现,针对每个命名空间下的元素提供不同BeanDefinitionParser来实现。

> 一开始就声明了各种常量字符串，都是在Spring的配置文件中常见的属性值。
```text
public static final String TRUE_VALUE = "true";

public static final String FALSE_VALUE = "false";

public static final String DEFAULT_VALUE = "default";

public static final String DESCRIPTION_ELEMENT = "description";

public static final String AUTOWIRE_NO_VALUE = "no";

public static final String AUTOWIRE_BY_NAME_VALUE = "byName";

public static final String AUTOWIRE_BY_TYPE_VALUE = "byType";

public static final String AUTOWIRE_CONSTRUCTOR_VALUE = "constructor";

public static final String AUTOWIRE_AUTODETECT_VALUE = "autodetect";

public static final String NAME_ATTRIBUTE = "name";

public static final String BEAN_ELEMENT = "bean";

public static final String META_ELEMENT = "meta";

public static final String ID_ATTRIBUTE = "id";

public static final String PARENT_ATTRIBUTE = "parent";

public static final String CLASS_ATTRIBUTE = "class";

public static final String ABSTRACT_ATTRIBUTE = "abstract";

public static final String SCOPE_ATTRIBUTE = "scope";

private static final String SINGLETON_ATTRIBUTE = "singleton";

public static final String LAZY_INIT_ATTRIBUTE = "lazy-init";

public static final String AUTOWIRE_ATTRIBUTE = "autowire";

public static final String AUTOWIRE_CANDIDATE_ATTRIBUTE = "autowire-candidate";

public static final String PRIMARY_ATTRIBUTE = "primary";

public static final String DEPENDS_ON_ATTRIBUTE = "depends-on";

public static final String INIT_METHOD_ATTRIBUTE = "init-method";

public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";

public static final String FACTORY_METHOD_ATTRIBUTE = "factory-method";

public static final String FACTORY_BEAN_ATTRIBUTE = "factory-bean";

public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";

public static final String INDEX_ATTRIBUTE = "index";

public static final String TYPE_ATTRIBUTE = "type";

public static final String VALUE_TYPE_ATTRIBUTE = "value-type";

public static final String KEY_TYPE_ATTRIBUTE = "key-type";

public static final String PROPERTY_ELEMENT = "property";

public static final String REF_ATTRIBUTE = "ref";

public static final String VALUE_ATTRIBUTE = "value";

public static final String LOOKUP_METHOD_ELEMENT = "lookup-method";

public static final String REPLACED_METHOD_ELEMENT = "replaced-method";

public static final String REPLACER_ATTRIBUTE = "replacer";

public static final String ARG_TYPE_ELEMENT = "arg-type";

public static final String ARG_TYPE_MATCH_ATTRIBUTE = "match";

public static final String REF_ELEMENT = "ref";

public static final String IDREF_ELEMENT = "idref";

public static final String BEAN_REF_ATTRIBUTE = "bean";

public static final String PARENT_REF_ATTRIBUTE = "parent";

public static final String VALUE_ELEMENT = "value";

public static final String NULL_ELEMENT = "null";

public static final String ARRAY_ELEMENT = "array";

public static final String LIST_ELEMENT = "list";

public static final String SET_ELEMENT = "set";

public static final String MAP_ELEMENT = "map";

public static final String ENTRY_ELEMENT = "entry";

public static final String KEY_ELEMENT = "key";

public static final String KEY_ATTRIBUTE = "key";

public static final String KEY_REF_ATTRIBUTE = "key-ref";

public static final String VALUE_REF_ATTRIBUTE = "value-ref";

public static final String PROPS_ELEMENT = "props";

public static final String PROP_ELEMENT = "prop";

public static final String MERGE_ATTRIBUTE = "merge";

public static final String QUALIFIER_ELEMENT = "qualifier";

public static final String QUALIFIER_ATTRIBUTE_ELEMENT = "attribute";

public static final String DEFAULT_LAZY_INIT_ATTRIBUTE = "default-lazy-init";

public static final String DEFAULT_MERGE_ATTRIBUTE = "default-merge";

public static final String DEFAULT_AUTOWIRE_ATTRIBUTE = "default-autowire";

public static final String DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE = "default-autowire-candidates";

public static final String DEFAULT_INIT_METHOD_ATTRIBUTE = "default-init-method";

public static final String DEFAULT_DESTROY_METHOD_ATTRIBUTE = "default-destroy-method";
```

> 填充给定的DocumentDefaultsDefinition实例，里面存了一个bean必须有的属性(lazyInit、merge、autowire、autowireCandidates、initMethod、destroyMethod、source)，没有就采用默认值。
```text
/**
 * 使用默认的lazy-init填充给定的DocumentDefaultsDefinition实例，
 * 自动装配，依赖项检查设置，初始化方法，销毁方法和合并设置。
 * 未在本地显式设置默认值的情况下，通过回退到{@code parentDefaults}，支持嵌套的“ beans”元素用例。
 *
 * @param defaults 默认填充
 * @param parentDefaults 父BeanDefinitionParserDelegate（如果有）默认返回
 * @param root 当前bean定义文档的根元素（或嵌套bean元素）
 */
protected void populateDefaults(DocumentDefaultsDefinition defaults, @Nullable DocumentDefaultsDefinition parentDefaults, Element root) {
    String lazyInit = root.getAttribute(DEFAULT_LAZY_INIT_ATTRIBUTE);
    if (isDefaultValue(lazyInit)) {
        // 可能从外部<beans>部分继承，否则返回false
        lazyInit = (parentDefaults != null ? parentDefaults.getLazyInit() : FALSE_VALUE);
    }
    // 1.为当前正在解析的文档设置默认的 lazy-init标志
    defaults.setLazyInit(lazyInit);

    String merge = root.getAttribute(DEFAULT_MERGE_ATTRIBUTE);
    if (isDefaultValue(merge)) {
        // 可能从外部<beans>节继承，否则返回false
        merge = (parentDefaults != null ? parentDefaults.getMerge() : FALSE_VALUE);
    }
    // 2.设置当前正在解析的文档的默认 合并设置
    defaults.setMerge(merge);

    String autowire = root.getAttribute(DEFAULT_AUTOWIRE_ATTRIBUTE);
    if (isDefaultValue(autowire)) {
        // Potentially inherited from outer <beans> sections, otherwise falling back to 'no'.
        autowire = (parentDefaults != null ? parentDefaults.getAutowire() : AUTOWIRE_NO_VALUE);
    }
    // 3.设置当前正在解析的文档的默认 自动装配设置
    defaults.setAutowire(autowire);

    // 4.设置当前正在解析的文档的默认自动装配候选模式。还接受逗号分隔的模式列表
    if (root.hasAttribute(DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE)) {
        defaults.setAutowireCandidates(root.getAttribute(DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE));
    }
    else if (parentDefaults != null) {
        defaults.setAutowireCandidates(parentDefaults.getAutowireCandidates());
    }

    // 5.设置当前正在解析的文档的默认 初始化方法设置
    if (root.hasAttribute(DEFAULT_INIT_METHOD_ATTRIBUTE)) {
        defaults.setInitMethod(root.getAttribute(DEFAULT_INIT_METHOD_ATTRIBUTE));
    }
    else if (parentDefaults != null) {
        defaults.setInitMethod(parentDefaults.getInitMethod());
    }

    // 6.设置当前正在解析的文档的默认 销毁方法设置
    if (root.hasAttribute(DEFAULT_DESTROY_METHOD_ATTRIBUTE)) {
        defaults.setDestroyMethod(root.getAttribute(DEFAULT_DESTROY_METHOD_ATTRIBUTE));
    }
    else if (parentDefaults != null) {
        defaults.setDestroyMethod(parentDefaults.getDestroyMethod());
    }

    // 7.设置此元数据元素的配置源{@code Object}。
    //   对象的确切类型将取决于所使用的配置机制。
    defaults.setSource(this.readerContext.extractSource(root));
}
```

> 处理beanName和别名
```text
/**
 * 解析提供的<bean>元素。 可能返回 null
 * 如果在解析过程中发生错误。错误报告给{@link org.springframework.beans.factory.parsing.ProblemReporter}。
 */
@Nullable
public BeanDefinitionHolder parseBeanDefinitionElement(Element ele, @Nullable BeanDefinition containingBean) {
    // 获取当前节点的id和name属性
    String id = ele.getAttribute(ID_ATTRIBUTE);
    String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);

    // 获取别名列表
    List<String> aliases = new ArrayList<>();
    if (StringUtils.hasLength(nameAttr)) {
        String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, MULTI_VALUE_ATTRIBUTE_DELIMITERS);
        aliases.addAll(Arrays.asList(nameArr));
    }
    // 如果id为空，且存在别名，那么把第一个别名当做beanName，并将他从别名列表中移除
    String beanName = id;
    if (!StringUtils.hasText(beanName) && !aliases.isEmpty()) {
        beanName = aliases.remove(0);
        if (logger.isTraceEnabled()) {
            logger.trace("No XML 'id' specified - using '" + beanName +
                    "' as bean name and " + aliases + " as aliases");
        }
    }

    if (containingBean == null) {
        checkNameUniqueness(beanName, aliases, ele);
    }
    // 解析bean标签并封装成对象
    AbstractBeanDefinition beanDefinition = parseBeanDefinitionElement(ele, beanName, containingBean);
    if (beanDefinition != null) {
        // 如果beanName为空，那么久根据指定的规则来创建一个beanName
        if (!StringUtils.hasText(beanName)) {
            try {
                if (containingBean != null) {
                    beanName = BeanDefinitionReaderUtils.generateBeanName(
                            beanDefinition, this.readerContext.getRegistry(), true);
                }
                else {
                    beanName = this.readerContext.generateBeanName(beanDefinition);
                    String beanClassName = beanDefinition.getBeanClassName();
                    if (beanClassName != null &&
                            beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() &&
                            !this.readerContext.getRegistry().isBeanNameInUse(beanClassName)) {
                        aliases.add(beanClassName);
                    }
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Neither XML 'id' nor 'name' specified - " +
                            "using generated bean name [" + beanName + "]");
                }
            }
            catch (Exception ex) {
                error(ex.getMessage(), ele);
                return null;
            }
        }
        String[] aliasesArray = StringUtils.toStringArray(aliases);
        // 返回一个封装了beanDefinition、beanName、aliasesArray的对象
        return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
    }

    return null;
}
```

> 解析bean定义本身，而不考虑名称或别名。如果在解析bean定义期间发生问题，可能会返回null。
```text
@Nullable
public AbstractBeanDefinition parseBeanDefinitionElement(
        Element ele, String beanName, @Nullable BeanDefinition containingBean) {

    this.parseState.push(new BeanEntry(beanName));

    String className = null;
    // 解析className
    if (ele.hasAttribute(CLASS_ATTRIBUTE)) {
        className = ele.getAttribute(CLASS_ATTRIBUTE).trim();
    }
    String parent = null;
    // 解析parent属性
    if (ele.hasAttribute(PARENT_ATTRIBUTE)) {
        parent = ele.getAttribute(PARENT_ATTRIBUTE);
    }

    try {
        AbstractBeanDefinition bd = createBeanDefinition(className, parent);

        parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
        // 设置描述符
        bd.setDescription(DomUtils.getChildElementValueByTagName(ele, DESCRIPTION_ELEMENT));
        // 解析其他属性
        parseMetaElements(ele, bd);
        parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
        parseReplacedMethodSubElements(ele, bd.getMethodOverrides());

        parseConstructorArgElements(ele, bd);
        parsePropertyElements(ele, bd);
        parseQualifierElements(ele, bd);

        bd.setResource(this.readerContext.getResource());
        bd.setSource(extractSource(ele));

        return bd;
    }
    catch (ClassNotFoundException ex) {
        error("Bean class [" + className + "] not found", ele, ex);
    }
    catch (NoClassDefFoundError err) {
        error("Class that bean class [" + className + "] depends on not found", ele, err);
    }
    catch (Throwable ex) {
        error("Unexpected failure during bean definition parsing", ele, ex);
    }
    finally {
        this.parseState.pop();
    }

    return null;
}
```

> 解析默认的属性
```text
/**
 * 将给定bean元素的属性应用于给定bean *定义
 * @param ele bean的声明元素
 * @param beanName bean名称
 * @param containingBean 包含bean定义
 * @return 根据bean元素属性初始化的bean实例
 */
public AbstractBeanDefinition parseBeanDefinitionAttributes(Element ele, String beanName,
        @Nullable BeanDefinition containingBean, AbstractBeanDefinition bd) {

    if (ele.hasAttribute(SINGLETON_ATTRIBUTE)) {
        error("Old 1.x 'singleton' attribute in use - upgrade to 'scope' declaration", ele);
    }
    else if (ele.hasAttribute(SCOPE_ATTRIBUTE)) {
        bd.setScope(ele.getAttribute(SCOPE_ATTRIBUTE));
    }
    else if (containingBean != null) {
        // 如果是内部bean定义，则从包含bean中获取默认值。
        bd.setScope(containingBean.getScope());
    }

    if (ele.hasAttribute(ABSTRACT_ATTRIBUTE)) {
        bd.setAbstract(TRUE_VALUE.equals(ele.getAttribute(ABSTRACT_ATTRIBUTE)));
    }

    String lazyInit = ele.getAttribute(LAZY_INIT_ATTRIBUTE);
    if (isDefaultValue(lazyInit)) {
        lazyInit = this.defaults.getLazyInit();
    }
    bd.setLazyInit(TRUE_VALUE.equals(lazyInit));

    String autowire = ele.getAttribute(AUTOWIRE_ATTRIBUTE);
    bd.setAutowireMode(getAutowireMode(autowire));

    if (ele.hasAttribute(DEPENDS_ON_ATTRIBUTE)) {
        String dependsOn = ele.getAttribute(DEPENDS_ON_ATTRIBUTE);
        bd.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn, MULTI_VALUE_ATTRIBUTE_DELIMITERS));
    }

    String autowireCandidate = ele.getAttribute(AUTOWIRE_CANDIDATE_ATTRIBUTE);
    if (isDefaultValue(autowireCandidate)) {
        String candidatePattern = this.defaults.getAutowireCandidates();
        if (candidatePattern != null) {
            String[] patterns = StringUtils.commaDelimitedListToStringArray(candidatePattern);
            bd.setAutowireCandidate(PatternMatchUtils.simpleMatch(patterns, beanName));
        }
    }
    else {
        bd.setAutowireCandidate(TRUE_VALUE.equals(autowireCandidate));
    }

    if (ele.hasAttribute(PRIMARY_ATTRIBUTE)) {
        bd.setPrimary(TRUE_VALUE.equals(ele.getAttribute(PRIMARY_ATTRIBUTE)));
    }

    if (ele.hasAttribute(INIT_METHOD_ATTRIBUTE)) {
        String initMethodName = ele.getAttribute(INIT_METHOD_ATTRIBUTE);
        bd.setInitMethodName(initMethodName);
    }
    else if (this.defaults.getInitMethod() != null) {
        bd.setInitMethodName(this.defaults.getInitMethod());
        bd.setEnforceInitMethod(false);
    }

    if (ele.hasAttribute(DESTROY_METHOD_ATTRIBUTE)) {
        String destroyMethodName = ele.getAttribute(DESTROY_METHOD_ATTRIBUTE);
        bd.setDestroyMethodName(destroyMethodName);
    }
    else if (this.defaults.getDestroyMethod() != null) {
        bd.setDestroyMethodName(this.defaults.getDestroyMethod());
        bd.setEnforceDestroyMethod(false);
    }

    if (ele.hasAttribute(FACTORY_METHOD_ATTRIBUTE)) {
        bd.setFactoryMethodName(ele.getAttribute(FACTORY_METHOD_ATTRIBUTE));
    }
    if (ele.hasAttribute(FACTORY_BEAN_ATTRIBUTE)) {
        bd.setFactoryBeanName(ele.getAttribute(FACTORY_BEAN_ATTRIBUTE));
    }

    return bd;
}
```

> 还有许多方法中都是对不同标签提供的不同的方法。

