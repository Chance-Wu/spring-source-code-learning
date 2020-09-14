package com.chance.springbeans.beans.packageinfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 定义只能标注在package上的注解
 * <p>
 *
 * @author chance
 * @since 2020-09-10
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PkgAnnotation {

}
