package com.ragentek.mealsupplement.servlet;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ragentek.mealsupplement.db.ServiceFactory;
import com.ragentek.mealsupplement.dwr.Result;
import com.ragentek.mealsupplement.json.User;
import com.ragentek.mealsupplement.tools.StringTools;
import com.ragentek.mealsupplement.tools.TextUtil;
import com.ragentek.mealsupplement.tools.Util;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import sun.plugin.com.TypeConverter;

/**
 * Created by kui.li on 2014/9/3.
 */
public class BaseServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    protected final Logger logger = Logger.getLogger(this.getClass());

	//public static HttpSession session; //servlet中原则上不要有成员变量，因为servlet是单例性质的

    public final String KEY_OP = "op";
    public static String OP_NAME;

    public static final String OP_QUERY = "query";
    public static final String OP_GET = "get";
    public static final String OP_ADD = "add";
    public static final String OP_DELETE = "delete";
    public static final String OP_UPDATE = "update";
    public static final String OP_OTHER = "other";

    public static final String DATA = "data";
    public static final String PMS = "pms";

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        //servlet是单例性质的，所以一般不能使用成员变量，故此处注射机制不能使用
        //注入参数
        /*
        Enumeration<String> names = req.getParameterNames();
        while(names.hasMoreElements()) {
            String name = names.nextElement();
            Util.injectHttpRequestProperty(this, name, req.getParameterValues(name));
        }*/
        //注入service,该注入有一些实际意义，暂时保留
        ServiceFactory.injectService(this);

        //session = req.getSession();
        OP_NAME = req.getParameter(KEY_OP);
        OP_NAME = StringTools.isEmpty(OP_NAME) ? OP_QUERY : OP_NAME;
        if (OP_QUERY.equals(OP_NAME)) {
            query(req, resp);
        } else if (OP_GET.equals(OP_NAME)) {
            get (req, resp);
        } else if (OP_ADD.equals(OP_NAME)) {
            add (req, resp);
        } else if (OP_DELETE.equals(OP_NAME)) {
            delete(req, resp);
        } else if (OP_UPDATE.equals(OP_NAME)) {
            update(req, resp);
        } else if (OP_OTHER.equals(OP_NAME)) {
            other (req, resp);
        } else {
            try {
                Method method = getClass().getMethod(OP_NAME, HttpServletRequest.class, HttpServletResponse.class);
                if(method != null) {
                    method.invoke(this, req, resp);
                }
            } catch (Exception e) {
                System.out.println(getClass().getName()+".service Error : op="+OP_NAME+"\n"+e.toString());
                e.printStackTrace();
                logger.error(getClass().getName()+".service Error : op="+OP_NAME+"\n"+e.toString(), e);
            }
        }

    }

    /**
     * 将拥有getter方法且值不为null的属性注射到HttpServletRequest的attribute中
     * servlet是单例性质的，不能使用成员变量，所以该注入也没有意义了
     * @param req
     */
    /*
    protected final void injectAttrs(HttpServletRequest req) {
        Map<String, Object> map = Util.getHttpServletPropertites(this);
        for(String key : map.keySet()) {
            req.setAttribute(key, map.get(key));
        }
    }
    */

    protected String getParameter(HttpServletRequest req, String paramName) {
        String value = req.getParameter(paramName);
        if(value != null && "GET".equalsIgnoreCase(req.getMethod())) {
            value = TextUtil.decode(value);
        }
        return value;
    }

    protected String[] getParameterValues(HttpServletRequest req, String paramName) {
        String[] values = req.getParameterValues(paramName);
        if(values != null && values.length > 0 && "GET".equalsIgnoreCase(req.getMethod())) {
            for(int i=0;i<values.length;i++) {
                values[i] = TextUtil.decode(values[i]);
            }
        }
        return values;
    }

    protected void printParams(HttpServletRequest req) {
        Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String[] values = req.getParameterValues(name);
            System.out.println(name+"----------");
            if(values == null) {
                System.out.println("null");
            } else {
                for(int i=0;i<values.length;i++) {
                    System.out.print(values[i]);
                    if(i != values.length-1) {
                        System.out.print(",");
                    }
                }
                System.out.println();
            }
        }
    }

    protected <T> T getParam(HttpServletRequest req, String paramName, Class<T> valueType) {
        if(valueType.isArray()) {
            return Util.convert(valueType, getParameterValues(req, paramName));
        } else {
            return Util.convert(valueType, getParameter(req, paramName));
        }
    }

    //这里paramName只是bean部分的名字，如pms.username,pms.userid是完整param名，而bean部分是pms
    //注：暂时bean只遍历一层，也就是不会处理类似pms.name.firstname这样的bean
    protected <T> T getBeanFromParam(HttpServletRequest req, String beanParamName, Class<T> beanType) throws IOException {
        T bean = null;
        Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String[] nameSplit = name.split("\\.");
            System.out.println(name+":"+nameSplit.length);
            if(nameSplit.length == 2 && nameSplit[0].equals(beanParamName)) {
                String propName = nameSplit[1];
                if(propName.length() > 0) {
                    if(bean == null) {
                        try {
                            bean = beanType.newInstance();
                            System.out.println("bean instance....");
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    if(bean != null) {
                        try {
                            PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean, propName);
                            if(pd != null) {
                                Method writeMethod = pd.getWriteMethod();
                                Class propertyType = pd.getPropertyType();
                                if(propertyType.isArray()) {
                                    writeMethod.invoke(bean, TypeConverter.convertObject(propertyType, getParameterValues(req, name)));
                                } else {
                                    writeMethod.invoke(bean, TypeConverter.convertObject(propertyType, getParameter(req, name)));
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(getClass().getName()+".getBeanFromParam error : beanParamName="+beanParamName+",beanClass="+beanType+",propName="+propName+",value="+getParameter(req, name)+".\nException="+e.toString());
                            logger.error(getClass().getName()+".getBeanFromParam error : beanParamName="+beanParamName+",beanClass="+beanType+",propName="+propName+",value="+getParameter(req, name)+".\nException="+e.toString());
                        }
                    }
                }
            }
        }

        return bean;
    }

    protected void goToIndexPage(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException{
        String returnUrl = httpRequest.getContextPath() + "/index.jsp";
        // for ajax Result
        if (httpRequest.getHeader("x-requested-with") != null
                && httpRequest.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")
                && httpRequest.getHeader("accept") != null
                && httpRequest.getHeader("accept").contains("application/json")) {
            logger.info("ajax result: " + returnUrl);
            String json = Result.error("出现后台错误！");
            httpResponse.getOutputStream().write(json.getBytes("UTF-8"));
            httpResponse.setContentType("text/json; charset=UTF-8");
        } else {
            User user = (User) httpRequest.getSession().getAttribute("userInfo");
            if(user == null) { //登录超时
                logger.info("goPage: "+returnUrl);
                httpRequest.setCharacterEncoding("UTF-8");
                httpResponse.setContentType("text/html; charset=UTF-8"); // 转码
                httpResponse.getWriter()
                        .println("<script language=\"javascript\">if(window.opener==null){window.top.location.href=\""
                                + returnUrl + "\";}else{window.opener.top.location.href=\"" + returnUrl
                                + "\";window.close();}</script>");
            } else {
                httpResponse.sendRedirect("exception.jsp");
            }
            // httpResponse.sendRedirect(returnUrl);
        }
    }

    /**
     * 瀛愮被閲嶅啓鐢ㄤ簬鍋氭煡璇㈡搷浣滐紝榛樿鐨勫姩浣�
     *
     * @param req
     * @param resp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void query(HttpServletRequest req,
                                  HttpServletResponse resp) throws ServletException, IOException{

    }

    /**
     * 璇锋眰鍙傛暟鍖呭惈锛歰peration=update 鏃惰皟鐢�瀛愮被閲嶅啓鐢ㄤ簬鏇存柊鏁版嵁
     *
     * @param req
     * @param resp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void update(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    /**
     * 璇锋眰鍙傛暟鍖呭惈锛歰peration=delete 鏃惰皟鐢�瀛愮被閲嶅啓鐢ㄤ簬鍒犻櫎鏁版嵁
     *
     * @param req
     * @param resp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void delete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    /**
     * 璇锋眰鍙傛暟鍖呭惈锛歰peration=add 鏃惰皟鐢�瀛愮被閲嶅啓鐢ㄤ簬娣诲姞鏁版嵁
     *
     * @param req
     * @param resp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void add(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    /**
     * 璇锋眰鍙傛暟鍖呭惈锛歰peration=add 鏃惰皟鐢�瀛愮被閲嶅啓鐢ㄤ簬娣诲姞鏁版嵁
     *
     * @param req
     * @param resp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void get(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    /**
     * 璇锋眰鍙傛暟鍖呭惈锛歰peration=other 鏃惰皟鐢�瀛愮被閲嶅啓鐢ㄤ簬娣诲姞鏁版嵁
     *
     * @param req
     * @param resp
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void other(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }
}
