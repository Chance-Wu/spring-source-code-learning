package com.chance.springcore.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

/**
 * <p>
 * DigestUtils
 * 计算摘要的其他方法。对java.serurity.MessageDigest的封装，提供单向加密方法。
 * 主要供框架内部使用。
 * <p>
 *
 * @author chance
 * @since 2020-09-10
 */
public class DigestUtilsTest {

    @Test
    public void test() {
        String str = "chance";
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(str.getBytes());
        System.out.println(md5DigestAsHex);
    }
}
