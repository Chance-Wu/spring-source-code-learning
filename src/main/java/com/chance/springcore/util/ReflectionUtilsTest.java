package com.chance.springcore.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

/**
 * <p>
 * ReflectionUtils
 * 用于反射API和处理的简单实用程序类：反射异常。
 * 仅供框架内部使用。
 * <p>
 *
 * @author chance
 * @since 2020-09-10
 */
public class ReflectionUtilsTest {

    @Test
    public void test() {
        ReflectionUtils.handleReflectionException(new Exception());
    }
}
