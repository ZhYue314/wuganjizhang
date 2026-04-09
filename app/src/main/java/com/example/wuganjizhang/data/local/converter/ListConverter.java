package com.example.wuganjizhang.data.local.converter;

import androidx.room.TypeConverter;

public class ListConverter {
    
    @TypeConverter
    public static String fromList(java.util.List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return android.text.TextUtils.join(",", list);
    }

    @TypeConverter
    public static java.util.List<String> toList(String value) {
        if (value == null || value.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return java.util.Arrays.asList(value.split(","));
    }
}
