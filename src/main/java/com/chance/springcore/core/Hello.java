package com.chance.springcore.core;

import java.lang.annotation.*;

/**
 * <p>
 *
 * <p>
 *
 * @author chance
 * @since 2020-09-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Hello {

    String value();
}