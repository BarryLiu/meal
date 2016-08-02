package com.ragentek.mealsupplement.tools;

import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.db.bean.TBillType;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.dwr.Result;
import com.ragentek.mealsupplement.service.UserService;
import com.ragentek.mealsupplement.servlet.UserServlet;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import sun.plugin.com.TypeConverter;

import javax.servlet.http.HttpServlet;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by zixiao.zhang on 2016/3/10.
 */
public class Util {
    private static final Logger logger = Logger.getLogger(Util.class);
    public static void injectHttpRequestProperty(Object servlet, String names, String[] values) {
        if(servlet == null || names == null || "".equals(names)) {
            return;
        }
        int index = names.indexOf(".");
        if(index == 0) {
            return;
        } else if(index > 0) {
            try {
                String s1 = names.substring(0, index);
                String s2 = names.substring(index+1);
                PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(servlet, s1);
                if(pd != null) {
                    Method readMethod = pd.getReadMethod();
                    if(readMethod != null) {
                        Object o = readMethod.invoke(servlet);
                        if(o == null) {
                            Method writeMethod = pd.getWriteMethod();
                            if(writeMethod != null) {
                                Class c = pd.getPropertyType();
                                o = c.newInstance();
                                writeMethod.invoke(servlet, o);
                                injectHttpRequestProperty(o, s2, values);
                            }
                        } else {
                            injectHttpRequestProperty(o, s2, values);
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Util.initProperty Error : \ntarget=" + servlet.getClass().getName() + ",names=" + names + ",value=" + values[0] + ".\nException=" + ex.toString());
                logger.error("Util.initProperty Error : \ntarget=" + servlet.getClass().getName() + ",names=" + names + ",value=" + values[0]+".\nException="+ex.toString());
            }
        } else if(index < 0) {
            try {
                PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(servlet, names);
                if(pd != null) {
                    Method writeMethod = pd.getWriteMethod();
                    if(writeMethod != null) {
                        //writeMethod.invoke(object, value);
                        Class propertyType = pd.getPropertyType();
                        if(propertyType.isArray()) {
                            writeMethod.invoke(servlet, TypeConverter.convertObject(pd.getPropertyType(),values));
                        } else {
                            writeMethod.invoke(servlet, TypeConverter.convertObject(pd.getPropertyType(),values[0]));
                        }
                    }
                }
            } catch(Exception e) {
                System.out.println("Util.initProperty Error : \ntarget=" + servlet.getClass().getName() + ",names=" + names + ",value=" + values[0] + ".\nException=" + e.toString());
                logger.error("Util.initProperty Error : \ntarget=" + servlet.getClass().getName() + ",names=" + names + ",value=" + values[0]+".\nException="+e.toString());
            }
        }
    }

    public static Map<String, Object> getHttpServletPropertites(HttpServlet servlet) {
        Map<String, Object> map = new HashMap<String, Object>();
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(servlet);
        for(PropertyDescriptor pd : pds) {
            String name = pd.getName();
            List<String> ignoreList = new ArrayList<String>();
            ignoreList.add("class");
            ignoreList.add("initParameterNames");
            ignoreList.add("servletConfig");
            ignoreList.add("servletContext");
            ignoreList.add("servletInfo");
            ignoreList.add("servletName");
            if(name != null && !name.equals("") && !ignoreList.contains(name)) {
                Method readMethod = pd.getReadMethod();
                if (readMethod != null) {
                    Object value = null;
                    try {
                        value = readMethod.invoke(servlet);
                        if(value != null) {
                            map.put(name, value);
                        }
                    } catch (Exception e) {
                        System.out.println(pd.getDisplayName() + ":Exception=" + e.toString());
                    }
                }
            }
        }
        return map;
    }

    public static boolean sameString(String s1, String s2) {
        boolean same = true;
        if(s1 == null && s2 != null) {
            same = false;
        } else if(s1 != null && s2 == null) {
            same = false;
        } else if(s1 != null && s2 != null && !s1.equals(s2)) {
            same = false;
        }
        return same;
    }

    public static Long[] stringArrayToLongArray(String[] strings) {
        return (Long[]) TypeConverter.convertObject(Long[].class, strings);
        /*Long[] res = new Long[strings.length];
        for(int i=0;i<strings.length;i++) {
            res[i] = Long.parseLong(strings[i]);
        }
        return res;*/
    }

    public static <T> T convert(Class<T> tClass, Object value) {
        return (T) TypeConverter.convertObject(tClass, value);
    }

//    public static  void  main(String[] args) throws Exception {
//    }

}
