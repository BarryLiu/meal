package com.ragentek.mealsupplement.serviceImpl;

import com.ragentek.mealsupplement.db.DBUtils;
import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.json.Dept;
import com.ragentek.mealsupplement.service.DeptService;
import com.ragentek.mealsupplement.tools.TextUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by yingjing.liu on 2016/7/22.
 */
public class DeptServiceImpl implements DeptService{


    @Override
    public List<TDept> getDeptsByType(int type ,String  parent_value) {
        String sql = "select * from t_dept ";
        if(type == Dept.TYPE_LEVEL_FIRST){ //所有的一级部门
            sql += " where dtype =  "+Dept.TYPE_LEVEL_FIRST ;
        }else if(type == Dept.TYPE_LEVEL_SECOND){
            sql += " where dtype =  "+Dept.TYPE_LEVEL_SECOND +" and parent_value = "+ parent_value;
        }

        return DBUtils.query(sql,TDept.class);
    }
    @Override
    public void inset(TDept dept) {
        DBUtils.insert(dept);
    }
    @Override
    public void update(TDept dept) {
        DBUtils.update(dept);
    }

    @Override
    public TDept selectById(String id) {
        String sql = "select * from t_dept where _id = "+id;
        List<TDept> depts = DBUtils.query(sql,TDept.class);
        if(depts.size()==0)
            return null ;
        else
            return depts.get(0);
    }

    @Override
    public String selectNextValue(String parentId,String parentValue) throws SQLException {
        String nextValue = "";
        String sql = "";
        if(!TextUtil.isNullOrEmpty(parentId)){   // 二级部门
            sql = " select max(value) from t_dept where parent_value = (select value from t_dept where _id = "+parentId+") ";
        }else{
            sql = " select max(value) from t_dept where dtype =  "+Dept.TYPE_LEVEL_FIRST; //一级部门
        }
        Connection conn =  DBUtils.openConnection();
       PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){ //有值代表不是第一个
            String value = rs.getString(1);
            if(value !=null){
                Integer val = Integer.valueOf(value);
                if(parentId!=null)          //如果是二级部门,递增 1  一级部门依次 递增100；
                    nextValue = (val+1)+"";
                else
                    nextValue = (val+100)+"";
            }else{         //没有值代表是这个部门下的第一个或者是一级部门 一般只有数据库第一条数据的时候才来
                if(!TextUtil.isNullOrEmpty(parentId)){      //第一个二级部门的用户
                    nextValue = (Integer.valueOf(parentValue)+1)+"";
                }else{                                      //第一个一级部门的用户
                    nextValue ="10100";
                }
            }
        }
        return nextValue;
    }

    @Override
    public void deleteById(String deptId) {
        DBUtils.delete(selectById(deptId));
    }

}
