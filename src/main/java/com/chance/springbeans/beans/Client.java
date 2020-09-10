package com.chance.springbeans.beans;

import java.lang.annotation.Annotation;

/**
 * <p>
 *
 * <p>
 *
 * @author chance
 * @since 2020-09-10
 */
public class Client {
    public static void main(String[] args) {
        //可以通过I/O操作或配置项获得包名
        String pkgName = "com.company";
        Package pkg = Package.getPackage(pkgName);
        //获得包上的注解
        Annotation[] annotations = pkg.getAnnotations();
        //遍历注解数组
        for (Annotation an : annotations) {
            if (an instanceof PkgAnnotation) {
                System.out.println("Hi,I'm the PkgAnnotation ,which is be placed on package!");
                /*
                 * 注解操作
                 * MyAnnotation myAnn = (PkgAnnotation)an;
                 * 还可以操作该注解包下的所有类，比如初始化，检查等等
                 * 类似Struts的@Namespace，可以放到包名上，标明一个包的namespace路径
                 */
            }
        }
    }

}
