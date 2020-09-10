package com.chance.springcore.util;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;

/**
 * <p>
 * CollectionUtils
 * 提供集合工具类,提供集合的转换、查找、判空等方法。
 * <p>
 *
 * @author chance
 * @since 2020-09-10
 */
public class CollectionUtilsTest {

    public static void main(String[] args) {
        HashMap<Object, Object> map = new HashMap<>(16);
        // 判空
        boolean isEmpty = CollectionUtils.isEmpty(map);
        System.out.println(isEmpty);
    }
}
