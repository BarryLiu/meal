package com.ragentek.mealsupplement.db;

import com.ragentek.mealsupplement.json.Leave;
import com.ragentek.mealsupplement.service.DeptService;
import com.ragentek.mealsupplement.servlet.*;
import com.ragentek.mealsupplement.tools.Util;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zixiao.zhang on 2016/3/11.
 */
public class ServiceConfig {
    public static final String SERVICE_USER = "com.ragentek.mealsupplement.serviceImpl.UserServiceImpl";
    public static final String SERVICE_USER_GROUP = "com.ragentek.mealsupplement.serviceImpl.UserGroupServiceImpl";
    public static final String SERVICE_GROUP = "com.ragentek.mealsupplement.serviceImpl.GroupServiceImpl";
    public static final String SERVICE_PERMISSION = "com.ragentek.mealsupplement.serviceImpl.PermissionServiceImpl";

    public static final String SERVICE_BILL_TYPE = "com.ragentek.mealsupplement.serviceImpl.BillTypeServiceImpl";
    public static final String SERVICE_BILL = "com.ragentek.mealsupplement.serviceImpl.BillServiceImpl";

    public static final String SERVICE_LEAVE = "com.ragentek.mealsupplement.serviceImpl.LeaveServiceImpl";

    public static final String SERVICE_FEE = "com.ragentek.mealsupplement.serviceImpl.FeeServiceImpl";

    public static final String SERVICE_STATISTIC = "com.ragentek.mealsupplement.serviceImpl.StatisticServiceImpl";

    public static final String SERVICE_REST="com.ragentek.mealsupplement.serviceImpl.RestServiceImpl";

    public static final String SERVICE_DEPT = "com.ragentek.mealsupplement.serviceImpl.DeptServiceImpl";

    private static Map<String, Map<String, String>> map;
    static {
        map = new HashMap<String, Map<String, String>>(); //Map<className, Map<servletServicePropertyName, serviceClassName>
        /** Login Servlet **/
        put(LoginServlet.class.getName(), "permissionService", SERVICE_PERMISSION);
        put(LoginServlet.class.getName(), "userService", SERVICE_USER);
        put(LoginServlet.class.getName(), "deptService", SERVICE_DEPT );
        /** User Servlet **/
        put(UserServlet.class.getName(), "userService", SERVICE_USER);
        put(UserServlet.class.getName(), "deptService", SERVICE_DEPT);
        /** BillType Servlet **/
        put(BillTypeServlet.class.getName(), "billTypeService", SERVICE_BILL_TYPE);
        /** Bill Servlet **/
        put(BillServlet.class.getName(), "billService", SERVICE_BILL);
        put(BillServlet.class.getName(), "billTypeService", SERVICE_BILL_TYPE);
        put(BillServlet.class.getName(), "userService", SERVICE_USER);
        put(BillServlet.class.getName(), "deptService", SERVICE_DEPT);
        /** LeaveServlet **/
        put(LeaveServlet.class.getName(), "leaveService", SERVICE_LEAVE);
        put(LeaveServlet.class.getName(), "userService", SERVICE_USER);
        put(LeaveServlet.class.getName(), "deptService", SERVICE_DEPT);
        /** MyLeaveServlet **/
        put(MyLeaveServlet.class.getName(), "leaveService", SERVICE_LEAVE);
        /** StatisticServlet **/
        put(StatisticServlet.class.getName(), "statisticService", SERVICE_STATISTIC);
        put(StatisticServlet.class.getName(), "userService", SERVICE_USER);
        put(StatisticServlet.class.getName(),"deptService",SERVICE_DEPT );

        /** MyStatisticServlet **/
        put(MyStatisticServlet.class.getName(), "statisticService", SERVICE_STATISTIC);
        /** PermissionServlet **/
        put(PermissionServlet.class.getName(), "permissionService", SERVICE_PERMISSION);
        put(PermissionServlet.class.getName(), "userService", SERVICE_USER);
        put(PermissionServlet.class.getName(), "groupService", SERVICE_GROUP);
        /**SearchServlet*/
        put(SearchServlet.class.getName(),"deptService",SERVICE_DEPT);
        put(SearchServlet.class.getName(),"userService",SERVICE_USER);
        /**RestServlet */
        put(RestServlet.class.getName(),"restService",SERVICE_REST);  //20160719 add
        put(RestServlet.class.getName(),"userService", SERVICE_USER);  //20160719 add
        /**DeptServlet */
        put(DeptServlet.class.getName(),"deptService",SERVICE_DEPT);
        put(DeptServlet.class.getName(),"userService",SERVICE_USER);

        /**OutputServlet */
        put(OutputServlet.class.getName(),"deptService",SERVICE_DEPT);
    }

    private static void put(String className, String propName, String serviceClassName) {
        Map<String, String> configMap = map.get(className);
        if(configMap == null) {
            configMap = new HashMap<String, String>();
            map.put(className, configMap);
        }
        configMap.put(propName, serviceClassName);
    }

    public static Map<String, String> getServiceConfig(String className) {
        return map.get(className);
    }
}
