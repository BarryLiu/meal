package com.ragentek.mealsupplement.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by zixiao.zhang on 2016/5/10.
 */
public class LeaveListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LeaveTimer.getInstance().startTimer();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        LeaveTimer.getInstance().stopTimer();
    }
}
