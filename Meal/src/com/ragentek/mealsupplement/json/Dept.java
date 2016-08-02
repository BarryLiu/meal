package com.ragentek.mealsupplement.json;
import com.ragentek.mealsupplement.db.SqlBean;
import com.ragentek.mealsupplement.db.bean.TDept;
import com.ragentek.mealsupplement.db.bean.TLeave;

import java.sql.Timestamp;
import java.util.List;


public class Dept extends JsonBean {

	public static final int TYPE_LEVEL_FIRST=1;	//一级部门
	public static final int TYPE_LEVEL_SECOND=2;//二级部门
	public static final int TYPE_ALL = 3;	//所有, 用于判断查询


	public Dept(TDept td) {
		Id = td.getId();
		this.name = td.getName();
		this.comment = td.getComment();
		this.value = td.getValue();
		this.parentValue = td.getParentValue();
		this.duid1 = td.getDuid1();
		this.duname1 = td.getDuname1();
		this.duid2 = td.getDuid2();
		this.duname2 = td.getDuname2();
		this.duid3 = td.getDuid3();
		this.duname3 = td.getDuname3();
		this.duids = td.getDuids();
		this.dunames = td.getDunames();
		this.createDate = td.getCreateDate();
		this.createBy = td.getCreateBy();
		this.updateDate = td.getUpdateDate();
		this.updateBy = td.getUpdateBy();
		this.parentId = td.getParentId();
		this.dtype = td.getDtype();
	}

	@Override

	public TDept toSqlBean() {

		TDept tDept = new TDept();
		tDept.setId(Id);
		tDept.setName(name);
		tDept.setComment(comment);
		tDept.setValue(value);
		tDept.setParentValue(parentValue);
		tDept.setDuid1(duid1);
		tDept.setDuname1(duname1);
		tDept.setDuid2(duid2);
		tDept.setDuname2(duname2);
		tDept.setDuid3(duid3);
		tDept.setDuname3(duname3);
		tDept.setDuids(duids);
		tDept.setDunames(dunames);
		tDept.setParentId(parentId);
		tDept.setDtype(dtype);
		return tDept;
	}

	private Integer Id;
	private String name;
	private String comment;
	private String value;
	private String parentValue;
	private Integer duid1;
	private String duname1;
	private Integer duid2;
	private String duname2;
	private Integer duid3;
	private String duname3;
	private String duids;
	private String dunames;
	private java.sql.Timestamp createDate;
	private String createBy;
	private java.sql.Timestamp updateDate;
	private String updateBy;
	private Integer parentId;
	private Integer dtype;
	private List<TDept> children;



	public Integer getId() {
		return Id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public Integer getDtype() {
		return dtype;
	}

	public void setDtype(Integer dtype) {
		this.dtype = dtype;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public void setId(Integer Id) {
		this.Id = Id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getParentValue() {
		return parentValue;
	}

	public void setParentValue(String parentValue) {
		this.parentValue = parentValue;
	}
	
	public Integer getDuid1() {
		return duid1;
	}

	public void setDuid1(Integer duid1) {
		this.duid1 = duid1;
	}
	
	public String getDuname1() {
		return duname1;
	}

	public void setDuname1(String duname1) {
		this.duname1 = duname1;
	}
	
	public Integer getDuid2() {
		return duid2;
	}

	public void setDuid2(Integer duid2) {
		this.duid2 = duid2;
	}
	
	public String getDuname2() {
		return duname2;
	}

	public void setDuname2(String duname2) {
		this.duname2 = duname2;
	}
	
	public Integer getDuid3() {
		return duid3;
	}

	public void setDuid3(Integer duid3) {
		this.duid3 = duid3;
	}
	
	public String getDuname3() {
		return duname3;
	}

	public void setDuname3(String duname3) {
		this.duname3 = duname3;
	}
	
	public String getDuids() {
		return duids;
	}

	public void setDuids(String duids) {
		this.duids = duids;
	}
	
	public String getDunames() {
		return dunames;
	}

	public void setDunames(String dunames) {
		this.dunames = dunames;
	}
	
	public java.sql.Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}
	
	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	
	public java.sql.Timestamp getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(java.sql.Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	
	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public void setChildren(List<TDept> children) {
		this.children = children;
	}
	public List<TDept> getChildren() {
		return children;
	}
}
