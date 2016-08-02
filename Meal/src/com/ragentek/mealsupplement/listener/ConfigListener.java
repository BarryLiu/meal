package com.ragentek.mealsupplement.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by zixiao.zhang on 2016/4/20.
 */
public class ConfigListener implements ServletContextListener {
    private static ServletContext servletContext;
    private static String fileRootDir;
    private static String startDay;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContext = servletContextEvent.getServletContext();
        fileRootDir = servletContext.getInitParameter("fileRootDir");
        if(fileRootDir == null) {
            fileRootDir = "D:\\meal\\";
        }
        startDay = servletContext.getInitParameter("startDay");
        if(startDay == null) {
            startDay = "2016/01/01";
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        servletContext = null;
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static String getFileRootDir() {
        return fileRootDir;
    }

    public static String getStartDay() {
        return startDay;
    }
}
