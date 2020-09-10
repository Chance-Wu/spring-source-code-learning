使用package-info类为包服务
--
Java中有一个特殊的类：package-info类，它是专门为本包服务的，为什么说它特殊呢？主要体现在3个方面：

> （1）它不能随便被创建
```text
在一般的IDE中，Eclipse、package-info等文件是不能随便被创建的，会报“Type name is notvalid”错误，类名无效。在Java变量定义规范中规定如下字符是允许的：字母、数字、下划线，以及那个不怎么常用的$符号，不过中划线可不在之列，那怎么创建这个文件呢？很简单，用记事本创建一个，然后拷贝进去再改一下就成了，更直接的办法就是从别的项目中拷贝过来。
```

> （2）它服务的对象很特殊
```text
一个类是一类或一组事物的描述，比如Dog这个类，就是描述“旺财”的，那package-info这个类是描述什么的呢？它总要有一个被描述或被陈述的对象吧，它是描述和记录本包信息的。
```

> （3）package-info类不能有实现代码

```text
package-info类再怎么特殊也是一个类，也会被编译成package-info.class，但是在package-info.java文件里不能声明package-info类。

package-info类还有几个特殊的地方，比如不可以继承，没有接口，没有类间关系（关联、组合、聚合等）等，不再赘述，Java中既然允许存在这么一个特殊的类，那肯定有其特殊的作用了，我们来看看它的作用，主要表现在以下三个方面：

1）声明友好类和包内访问常量
这个比较简单，而且很实用，比如一个包中有很多内部访问的类或常量，就可以统一放到package-info类中，这样很方便，而且便于集中管理，可以减少友好类到处游走的情况，代码如下：


//这里是包类，声明一个包使用的公共类  
class PkgClass{  
     public void test(){    }  
}  
//包常量，只允许包内访问  
class PkgConst{  
     static final String PACAKGE_CONST="ABC";  
}

注意以上代码是存放在package-info.java中的，虽然它没有编写package-info的实现，但是package-info.class类文件还是会生成。通过这样的定义，我们把一个包需要的类和常量都放置在本包下，在语义上和习惯上都能让程序员更适应。

2）为在包上标注注解提供便利
比如我们要写一个注解（Annotation），查看一个包下的所有对象，只要把注解标注到package-info文件中即可，而且在很多开源项目也采用了此方法，比如Struts2的@namespace、Hibernate的@FilterDef等。

3）提供包的整体注释说明
如果是分包开发，也就是说一个包实现了一个业务逻辑或功能点或模块或组件，则该包需要有一个很好的说明文档，说明这个包是做什么用的，版本变迁历史，与其他包的逻辑关系等，package-info文件的作用在此就发挥出来了，这些都可以直接定义到此文件中，通过javadoc生成文档时，会把这些说明作为包文档的首页，让读者更容易对该包有一个整体的认识。当然在这点上它与package.htm的作用是相同的，不过package-info可以在代码中维护文档的完整性，并且可以实现代码与文档的同步更新。

我们来建立一个项目演示这三个作用，建立一个package-info的Java Project，在com.company包三个类:package-info.java是我们重点关注的，PkgAnnotation.java是一个标注在包上的注解定义，Client.java模拟业务操作类。其结构如下图：


声明友好类和包常量：比如一个包中有很多的内部访问的类或常量，就可以统一的放到package-info类中，这样就方便，而且集中管理，减少friendly类到处游走的情况。

/**
 * <b>package-info不是平常类，其作用有三个:</b><br> 
 * 1、为标注在包上Annotation提供便利；<br> 
 * 2、声明包的私有类和常量；<br> 
 * 3、提供包的整体注释说明。<br>  
 */
@PkgAnnotation
package com.chance.springbeans.beans;

/**
 * 这里是包类，声明一个包使用的公共类，强调的是包访问权限
 */
class PkgClass {
    public void test() {

    }
}

/**
 * 包常量，只运行包内访问，适用于分"包"开发
 */
class PkgConst {
    static final String PACKAGE_CONST = "ABC";
}
```

package-info可以更好的在代码中维护文档的完整性，并且可以实现代码与文档同步更新，建议是Java 1.5以上版本都使用package-info.java来注释。 

与package-info相关的问题：
* 在项目开发中，可以放置在包上的常用注解有：Struts的@namespace、Hibernate的@FilterDef和@TypeDef等等。在包下，随便一个类中的包名前加这些注解，Eclipse会提示“Package annotations must be in file package-info.java”,在该包下建立package-info.java文件，把注解移到这里即可。
* 使用Checkstyle插件做代码检查时，会报一个警告“Missingpackage-info.java file.”也是这个package-info文件惹的祸，在各个包下创建一个即可。

解释了这么多，总结成一句话：在需要用到包的地方，就可以考虑一下package-info这个特殊类，也许能起到事半功倍的作用。

