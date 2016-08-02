<%@ page import="com.ragentek.mealsupplement.tools.KaoQin" %>
<%--
  Created by IntelliJ IDEA.
  User: kui.li
  Date: 2015/5/8
  Time: 9:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script type="text/javascript">
        var name = "哈哈";
        var name2 = "张子枭";
        var age = 12;
        var sex = "male";
        location.href = "${pageContext.request.contextPath}/billtype?name="+encodeURIComponent(encodeURIComponent(name))+"&age="+encodeURIComponent(encodeURIComponent(age))+"&sex="+encodeURIComponent(encodeURIComponent(sex))+"&op=test&name="+encodeURIComponent(encodeURIComponent(name2));
    </script>
</head>
<body>
<%
    /*
    String paras = request.getParameter("firstRow");
    if(paras != null && !"".equals(paras)) {
        out.print("firstRow=" + paras + "<br>");

        KaoQin.countKaoQinForFees(Long.valueOf(paras));
        out.print("餐补数据保存完成！！");
    } else {
        out.print("参数不正确！！");
    }*/
%>
</body>
</html>
