package com.ragentek.mealsupplement.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by zixiao.zhang on 2016/4/12.
 */
public class UserListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        UserTimer.getInstance().startTimer();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        UserTimer.getInstance().stopTimer();
    }
}
