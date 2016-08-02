package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class TDept implements SqlBean {
@Override
public String getTableName() {
	return "t_dept";
}
@Override
public String getPrimaryKeyColumnName() {
	return "_id";
}
@Override
public String[] getColumnNames() {
	return new String[]{
"_id",
"name",
"comment",
"value",
"parent_value",
"duid1",
"duname1",
"duid2",
"duname2",
"duid3",
"duname3",
"duids",
"dunames",
"create_date",
"create_by",
"update_date",
"update_by",
"dtype",
"parent_id"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("Id=");	
sb.append(Id);	
sb.append(",name=");	
sb.append(name);	
sb.append(",comment=");	
sb.append(comment);	
sb.append(",value=");	
sb.append(value);	
sb.append(",parentValue=");	
sb.append(parentValue);	
sb.append(",duid1=");	
sb.append(duid1);	
sb.append(",duname1=");	
sb.append(duname1);	
sb.append(",duid2=");	
sb.append(duid2);	
sb.append(",duname2=");	
sb.append(duname2);	
sb.append(",duid3=");	
sb.append(duid3);	
sb.append(",duname3=");	
sb.append(duname3);	
sb.append(",duids=");	
sb.append(duids);	
sb.append(",dunames=");	
sb.append(dunames);	
sb.append(",createDate=");	
sb.append(createDate);	
sb.append(",createBy=");	
sb.append(createBy);	
sb.append(",updateDate=");	
sb.append(updateDate);	
sb.append(",updateBy=");	
sb.append(updateBy);	
sb.append(",dtype=");	
sb.append(dtype);	
sb.append(",parentId=");	
sb.append(parentId);
	return sb.toString();
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
	private Integer dtype;
	private Integer parentId;
	
	public Integer getId() {
		return Id;
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
	
	public Integer getDtype() {
		return dtype;
	}

	public void setDtype(Integer dtype) {
		this.dtype = dtype;
	}
	
	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
}
