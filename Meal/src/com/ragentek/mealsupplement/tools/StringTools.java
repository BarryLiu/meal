package com.ragentek.mealsupplement.tools;

/**
 * Created by kui.li on 2014/10/10.
 */
public class StringTools {

    public static final boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }


    /**
     * 如果为空或者null 就返回 "" 的字符串
     * @param s
     * @return
     */
    public static final String  getString(String s){
        return isEmpty(s)?"":s;
    }

    /**
     * 如果是 0 或 null 就返回 空字符串
     * @param i
     * @return
     */
    public static final String getString(Integer i){
        return i==null||i==0?"":i+"";
    }
    
    
}
