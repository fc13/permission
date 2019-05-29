package com.example.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    public static List<Integer> spiltToListInt(String str) {
        if (StringUtils.isBlank(str)) {
            return Lists.newArrayList();
        }
        List<String> strs = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str);
        return strs.stream().map(Integer::parseInt).collect(Collectors.toList());
    }
}
