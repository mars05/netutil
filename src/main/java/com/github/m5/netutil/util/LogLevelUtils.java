package com.github.m5.netutil.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * @author xiaoyu
 */
public class LogLevelUtils {
    public static void setRootLevel(Level level) {
        try {
            Field rootLoggerField = LoggerContext.class.getDeclaredField("root");
            rootLoggerField.setAccessible(true);
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) rootLoggerField.get(LoggerFactory.getILoggerFactory());
            rootLogger.setLevel(level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
