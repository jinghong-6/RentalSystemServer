package com.example.rental.utils;

public class CharacterFilter {
    public static String filterSpecialCharacters(String input) {
        // 定义特殊字符的正则表达式
        String regex = "[`~!$%^&*《》()_\\-+={}|:;\"<>?\\[\\]·~@#￥…&*（）—【】+={}|“”‘’]";

        // 使用正则表达式替换特殊字符为空字符串
        String filteredString = input.replaceAll(regex, "");

        return filteredString;
    }
}
