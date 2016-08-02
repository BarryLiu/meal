package com.ragentek.mealsupplement.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by zixiao.zhang on 2016/3/10.
 */
public class TextUtil {
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public static String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static boolean isValidNumber(String number) {
        if(isNullOrEmpty(number)) {
            return false;
        }
        //目前1-12位数字认为是合法的工号
        String regex = "^[0-9]{1,12}$";
        return number.matches(regex);
    }

}
