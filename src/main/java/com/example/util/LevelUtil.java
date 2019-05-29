package com.example.util;

import org.apache.commons.lang3.StringUtils;

public class LevelUtil {
    private final static String SEPARATOR = ".";

    public final static String ROOT = "0";

    // level计算规则
    public static String calculateLevel(String parentLevel, Integer parentId) {
        if (StringUtils.isBlank(parentLevel)) {
            return ROOT;
        } else {
            return StringUtils.join(parentLevel, SEPARATOR, parentId);
        }
    }
}
