package com.github.m5.netutil.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author xiaoyu
 */
public class UnsafeUtils {
    private static final Unsafe theUnsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            theUnsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Unsafe getUnsafe() {
        return theUnsafe;
    }

}
