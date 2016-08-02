package com.ragentek.mealsupplement.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Created by zixiao.zhang on 2016/3/10.
 * 设计思路：通过className找到serviceKey集合，遍历serviceKey，通过key获得service对象，并通过key（将key当做变量名）通过setter方法将其注射到Servlet中
 * 注意点：在servlet中声明service，其变量名要与此处的serviceKey一致，否则无法主动注射
 */
public class ServiceFactory {
    private static final Logger logger = Logger.getLogger(ServiceFactory.class);
    private static Map<String, Object> maps = new HashMap<String, Object>();

    public static <T> T getService(String serviceClassName) {
        T service = null;
        if(maps.containsKey(serviceClassName)) {
            service = (T) maps.get(serviceClassName);
        } else {
            Class<T> cla = null;
            try {
                cla = (Class<T>) Class.forName(serviceClassName);
            } catch (Exception e) {
                System.out.println("ServiceFactory.getService Class.forName : \nserviceClassName=" + serviceClassName + ".\nException=" + e.toString());
                logger.error("ServiceFactory.getService Class.forName : \nserviceClassName=" + serviceClassName + ".\nException=" + e.toString());
            }
            if(cla != null) {
                try {
                    service = cla.newInstance();
                } catch (Exception e) {
                    System.out.println("ServiceFactory.getService newInstance : \nserviceClassName="+serviceClassName+".\nException="+e.toString());
                    logger.error("ServiceFactory.getService newInstance : \nserviceClassName=" + serviceClassName + ".\nException="+e.toString());
                }
            }
            maps.put(serviceClassName, service);
        }
        return service;
    }

    public static void injectService(Object object) {
        if(object == null) {
            return;
        }
        String className = object.getClass().getName();
        Map<String, String> configMap = ServiceConfig.getServiceConfig(className);
        if(configMap != null && configMap.size() > 0) {
            for(String propName : configMap.keySet()) {
                String serviceClassName = configMap.get(propName);
                Object service = getService(serviceClassName);
                try {
                    Field field = object.getClass().getDeclaredField(propName);
                    if(!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    if(field.get(object) == null) {
                        field.set(object, service);
                    }
                } catch (Exception e) {
                    System.out.println("ServiceFactory.injectService Error: target="+className+",propName="+propName+",service="+serviceClassName+",Exception="+e.toString());
                    logger.error("ServiceFactory.injectService Error: target="+className+",propName="+propName+",service="+serviceClassName+",Exception="+e.toString());
                }

                /*
                PropertyDescriptor pd = null;
                try {
                    pd = PropertyUtils.getPropertyDescriptor(object, propName);
                } catch (Exception e) {
                    System.out.println("ServiceFactory.injectService getPropertyDescriptor Error : \ntarget className="+className+",propName="+propName+".\nException="+e.toString());
                    logger.error("ServiceFactory.injectService getPropertyDescriptor Error : \ntarget className="+className+",propName="+propName+".\nException="+e.toString());
                }
                if(service != null && pd != null) {
                    Method writeMethod = pd.getWriteMethod();
                    if(writeMethod != null) {
                        try {
                            writeMethod.invoke(object, service);
                        } catch (Exception e) {
                            System.out.println("ServiceFactory.injectService writeMethod.invoke Error : \ntarget className="+className+",propName="+propName+",propClass="+pd.getPropertyType().getName()+",serviceClss="+service.getClass().getName()+".\nException="+e.toString());
                            logger.error("ServiceFactory.injectService writeMethod.invoke Error : \ntarget className=" + className + ",propName=" + propName + ",propClass=" + pd.getPropertyType().getName()+ ",serviceClss=" + service.getClass().getName() + ".\nException="+e.toString());
                        }
                    }
                }
                */
            }
        }
    }
}
