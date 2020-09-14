package com.chance.springcore.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.SystemPropertyUtils;

/**
 * <p>
 * SystemPropertyUtils
 * 用于在文本中解析占位符的帮助程序类。通常应用于文件路径。
 * 文本可能包含{@code $ {...}}占位符，将其解析为系统属性：
 *   *例如 {@code $ {user.dir}}。 可以使用“：”分隔符提供默认值
 *   *在键和值之间。
 * <p>
 *
 * @author chance
 * @since 2020-09-10
 */
public class SystemPropertyUtilsTest {

    @Test
    public void test() {
        String placeholders = SystemPropertyUtils.resolvePlaceholders("${path:prefix}/${path:suffix}");
        System.out.println(placeholders);
    }
}
