在写程序的时候你可以定义是否可为空指针。通过使用像@NotNull和@Nullable之类的annotation来声明一个方法是否是空指针安全的。现代的编译器、IDE或者工具可以读此annotation并帮你添加忘记的空指针检查，或者向你提示出不必要的乱七八糟的空指针检查。IntelliJ和findbugs已经支持了这些annotation。这些annotation同样是JSR 305的一部分，但即便IDE或工具中没有，这个annotation本身可以作为文档。看到@NotNull和@Nullable，程序员自己可以决定是否做空指针检查。顺便说一句，这个技巧对Java程序员来说相对比较新，要采用需要一段时间。

> 如果可以传入NULL值，则标记为@Nullable，如果不可以，则标注为@Nonnull。那么在我们做一些不安全严谨操作的编码操作时，这些注释会给我们一些警告。如下是我看spring源码时，发现用到@Nullable，借此源码做个测试：
```java
public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh, @Nullable ApplicationContext parent) throws BeansException {
    super(parent);
    this.setConfigLocations(configLocations);
    if (refresh) {
        this.refresh();
    }
 
}
```

> 我们把@Nullable改成@Nonnull，然后发现调用该方法的地方出现告警：
```java
public FileSystemXmlApplicationContext(String... configLocations) throws BeansException {
     // here warning   
     this(configLocations, true, (ApplicationContext)null);
}
```

