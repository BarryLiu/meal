package com.ragentek.mealsupplement.db.bean ;
import com.ragentek.mealsupplement.db.SqlBean;


public class TPermission implements SqlBean {
@Override
public String getTableName() {
	return "t_permission";
}
@Override
public String getPrimaryKeyColumnName() {
	return "_id";
}
@Override
public String[] getColumnNames() {
	return new String[]{
"_id",
"per_name",
"per_value",
"parent_value",
"per_type",
"url",
"per_desc",
"isexpand"};
}
@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	
sb.append("Id=");	
sb.append(Id);	
sb.append(",perName=");	
sb.append(perName);	
sb.append(",perValue=");	
sb.append(perValue);	
sb.append(",parentValue=");	
sb.append(parentValue);	
sb.append(",perType=");	
sb.append(perType);	
sb.append(",url=");	
sb.append(url);	
sb.append(",perDesc=");	
sb.append(perDesc);	
sb.append(",isexpand=");	
sb.append(isexpand);
	return sb.toString();
}
	private Long Id;
	private String perName;
	private Integer perValue;
	private Integer parentValue;
	private Integer perType;
	private String url;
	private String perDesc;
	private Integer isexpand;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long Id) {
		this.Id = Id;
	}
	
	public String getPerName() {
		return perName;
	}

	public void setPerName(String perName) {
		this.perName = perName;
	}
	
	public Integer getPerValue() {
		return perValue;
	}

	public void setPerValue(Integer perValue) {
		this.perValue = perValue;
	}
	
	public Integer getParentValue() {
		return parentValue;
	}

	public void setParentValue(Integer parentValue) {
		this.parentValue = parentValue;
	}
	
	public Integer getPerType() {
		return perType;
	}

	public void setPerType(Integer perType) {
		this.perType = perType;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getPerDesc() {
		return perDesc;
	}

	public void setPerDesc(String perDesc) {
		this.perDesc = perDesc;
	}
	
	public Integer getIsexpand() {
		return isexpand;
	}

	public void setIsexpand(Integer isexpand) {
		this.isexpand = isexpand;
	}
}
