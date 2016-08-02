package com.ragentek.mealsupplement.servlet;

import com.ragentek.mealsupplement.KaoQingDao;
import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.KaoqinDto;
import com.ragentek.mealsupplement.db.bean.TUser;
import com.ragentek.mealsupplement.dwr.Result;
import com.ragentek.mealsupplement.ldap.LDAPControl;
import com.ragentek.mealsupplement.listener.ConfigListener;
import com.ragentek.mealsupplement.poi.UserAttendanceExport;
import com.ragentek.mealsupplement.tools.KaoQin;
import com.ragentek.mealsupplement.tools.LeaveUtil;
import com.ragentek.mealsupplement.tools.TextUtil;
import com.ragentek.mealsupplement.tools.TotalFee;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kui.li on 2014/9/2.
 */
public class UploadServlet extends BaseServlet {
    private String savePath;
    private ServletContext sc;
//    private static final Logger logger = Logger.getLogger(LoginServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        savePath = config.getInitParameter("savePath");
        savePath = "upload";
        sc = config.getServletContext();
    }

    @Override
    protected void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String json = null;
        req.setCharacterEncoding("UTF-8");
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            /*
            String sDate = getParameter(req, "sDate");
            if(!TextUtil.isNullOrEmpty(sDate)) {
                req.setAttribute("sDate", sDate);
            }
            String eDate = getParameter(req, "eDate");
            if(!TextUtil.isNullOrEmpty(eDate)) {
                req.setAttribute("eDate", eDate);
            }
            System.out.println("sDate="+sDate+",eDate="+eDate);*/
            String sDate = null;
            String eDate = null;
            List<DiskFileItem> items = upload.parseRequest(req);// 上传文件解析
            for(DiskFileItem fileItem : items) {
                if(fileItem.isFormField()) {
                    if("sDate".equals(fileItem.getFieldName())) {
                        sDate = fileItem.getString("UTF-8");
                    } else if("eDate".equals(fileItem.getFieldName())) {
                        eDate = fileItem.getString("UTF-8");
                    }
                }
            }
            //ajax请求，没必要传回去更新界面
            /*
            if(!TextUtil.isNullOrEmpty(sDate)) {
                req.setAttribute("sDate", sDate);
            }
            if(!TextUtil.isNullOrEmpty(eDate)) {
                req.setAttribute("eDate", eDate);
            }
            */
            Iterator itr = items.iterator();// 枚举方法
            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                if (item.isFormField()) {// 判断是文件还是文本信息
                    System.out.println("表单参数名:" + item.getFieldName()
                            + "，表单参数值:" + item.getString("UTF-8"));
                } else {
                    if (item.getName() != null && !item.getName().equals("")) {// 判断是否选择了文件
                        System.out.println("上传文件的大小:" + item.getSize());
                        System.out.println("上传文件的类型:" + item.getContentType());
                        // item.getName()返回上传文件在客户端的完整路径名称
                        System.out.println("上传文件:" + item.getName()+"..........");
                        // 此时文件暂存在服务器的内存当中

                        File tempFile = new File(item.getName());// 构造临时对象
                        /*File file = new File(sc.getRealPath("/") + savePath,
                                tempFile.getName());*/
                        File dir = new File(ConfigListener.getFileRootDir(), savePath);
                        if(!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, tempFile.getName());
                        // 获取根目录对应的真实物理路径
                        item.write(file);// 保存文件在服务器的物理磁盘中
                        //req.setAttribute("message", "上传文件成功！");// 返回上传结果

                        // 文件上传成功，开始解析文件
                        System.out.println("解析考勤数据 ..........." );
                        //long firstRow = KaoQin.saveData(file, 0);
                        long firstRow = KaoQin.saveData(file, 0, sDate, eDate);
                        System.out.println("考勤数据保存完成！！ firstRow=" + firstRow);
                        if(firstRow<=0) {
                            /*
                            req.setAttribute("message", "您选择的文件格式不正确！");
                            req.getRequestDispatcher("upload.jsp").forward(req, resp);
                            return ;*/
                            logger.warn("导入成功："+item.getName()+",但是没有任何有效数据！");
                        }
                        // 开始计算统计餐补数据
                        //System.out.println("update fees......." );
                        //KaoQin.countKaoQinForFees(firstRow);
                        //System.out.println("餐补数据保存完成！！");

                        //req.getRequestDispatcher("upload_result.jsp").forward(req, resp);
                    } else {
                        //req.setAttribute("message", "没有选择上传文件！");
                        //req.getRequestDispatcher("upload.jsp").forward(req, resp);
                    }
                }
            }
            json = Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            json = Result.error("发生后台错误，导入失败！");
        }
        resp.getOutputStream().write(json.getBytes("UTF-8"));
    }


    /**
     * POI 导出考勤信息

     */
    public String exportPoi(HttpServletRequest req, HttpServletResponse resp){
        String  startDay=req.getParameter("startDay");
        String endDay =req.getParameter("endDay") ;
        int res = 0;
        try{
            //执行POI导出 操作
            System.out.println("is to export report for  POI to bill...");

            KaoQingDao dao = new KaoQingDao();
            List<KaoqinDto> dtos = dao.queryByDate(startDay,endDay);


            OutputStream os = new FileOutputStream(new File("D:\\麦穗考勤系統数据"+startDay.replaceAll("/","")+"-"+endDay.replaceAll("/","")+".xlsx"));
            UserAttendanceExport uae = new UserAttendanceExport(startDay,endDay);
            uae.setData(dtos);

            uae.exportExcel(os);


        }catch(Exception e){
            e.printStackTrace();
            res= LeaveUtil.NA_EXCEPTION;
        }
        if(res == LeaveUtil.NA_SUCCESS){
            return Result.success();
        }else{
            Result result = new Result(false,res);
            return result.toString();
        }
    }

}