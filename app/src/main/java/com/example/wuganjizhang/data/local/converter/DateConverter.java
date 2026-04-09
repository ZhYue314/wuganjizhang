package com.example.wuganjizhang.data.local.converter;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    
    @TypeConverter
    public static Long fromTimestamp(Date value) {
        return value == null ? null : value.getTime();
    }

    @TypeConverter
    public static Date dateFromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long fromTimestamp(long value) {
        return value;
    }

    @TypeConverter
    public static long dateFromTimestamp(long value) {
        return value;
    }
}
