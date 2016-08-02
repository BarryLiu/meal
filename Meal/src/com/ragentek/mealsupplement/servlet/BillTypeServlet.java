package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TBillType;
import com.ragentek.mealsupplement.dwr.Result;
import com.ragentek.mealsupplement.json.Rows;
import com.ragentek.mealsupplement.service.BillTypeService;
import com.ragentek.mealsupplement.tools.StringTools;
import com.ragentek.mealsupplement.tools.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zixiao.zhang on 2016/3/14.
 */
public class BillTypeServlet extends BaseServlet {
    private BillTypeService billTypeService;
    /** 特别说明，servlet是单例的（其实是只实例化一次），为了保证数据安全，数据不要写成成员变量形式，容易出错。如果加锁控制又降低效率，总之不要写成成员变量。 **/
    /*
    private TBillType pms;
    private String data;
    private Long[] ids;
    */

    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException {
        try {
            List<TBillType> lst = billTypeService.getAll();
            Rows row = new Rows(lst);
            String data = row.toJson();
            req.setAttribute(DATA, data);
            System.out.println("data==="+data);
            req.getRequestDispatcher("bill/billTypeList.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("UserServlet.query", e);
            req.setAttribute("error_msg", "登陆超时，请重新登陆！");
            //resp.sendRedirect("/index.jsp");
            goToIndexPage(req, resp);
        }
    }

    @Override
    protected void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TBillType pms = getBeanFromParam(req, PMS, TBillType.class);
        String json = null;
        String billName = pms.getBillName();
        if(billTypeService.existBillName(billName)) {
            json = Result.error("The Bill Name already exists, please change one!");
        } else {
            long id = DBUtils.insert(pms);
            if(id >= 0) {
                pms.setId(id);
                json = Result.success(pms);
            } else  {
                json = Result.error("Failed to add!");
                System.out.println("BillType add Failed: billName="+pms.getBillName()+",billType="+pms.getBillName()+",id="+id);
            }
        }
        resp.getOutputStream().write(json.getBytes("UTF-8"));
    }

    @Override
    protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Long[] ids = Util.stringArrayToLongArray(req.getParameterValues("ids"));
        Long[] ids = getParam(req, "ids", Long[].class);
        int count = billTypeService.deleteMulti(ids);
        if(count != ids.length) {
            logger.warn("BillTypeServlet:count="+count+"ids.length="+ids.length);
        }
        resp.getOutputStream().write(Result.success().getBytes("UTF-8"));
    }

    public void test(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String[] name = getParameterValues(req, "name");
//        String sex = getParameter(req, "sex");
//        Integer age = getParam(req, "age", Integer.class);
//        System.out.println(name[0]+":"+sex+":"+age+":"+name[1]);
    }

    public void setBillTypeService(BillTypeService billTypeService) {
        this.billTypeService = billTypeService;
    }

    /*
    public TBillType getPms() {
        return pms;
    }

    public void setPms(TBillType pms) {
        this.pms = pms;
    }

    public String getData() {
        return data;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }
    */
}
