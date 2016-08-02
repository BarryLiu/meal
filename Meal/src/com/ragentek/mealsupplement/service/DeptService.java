package com.ragentek.mealsupplement.service;

import com.ragentek.mealsupplement.db.bean.TDept;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yingjing.liu on 2016/7/22.
 */
public interface DeptService {
    /**
     * 根据类型 查询 部门 ,1 查询所有1级部门, 2 查询所有二级部门，3 所有部门
     * @param type
     * @param parent_value //只有type 为2 的时候才有意义
     * @return
     */
    public List<TDept> getDeptsByType(int type,String parent_value);
    public void inset(TDept dept);
    public void update(TDept dept);

    public  TDept selectById(String id);

    /**
     * 查询下一个 value 的值 ， 这个值本来是不存在的，根据系统中已经有的计算出来
     * @param parentId  如果不为空  查询其子部门中的 最大value+1 的值返回
     *                  如果为空代表一级部门      返回最外层的value+1的值
     * @return
     */
    public String selectNextValue(String parentId,String parentValue) throws SQLException;

    public   void deleteById(String deptId);
}
