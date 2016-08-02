<%--
  Created by IntelliJ IDEA.
  User: kui.li
  Date: 2014/11/7
  Time: 17:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>帮助信息</title>
    <link href="ligerUI/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" id="mylink"/>
    <style type="text/css">
        span {
            display:block;
            min-width:1000px;
        }
    </style>
</head>
<body>
<form method="post" id="form1" action="upload" onsubmit="return checkParams()" enctype="multipart/form-data">
    <p>&nbsp;</p>

    <h1>餐补系统介绍</h1>

    <p>&nbsp;</p>


    <h5>用户管理</h5>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【用户管理】-【用户】显示所有LDAP用户；LDAP用户会自动同步，在该页面也可手动同步；该页面还可以设置特定用户不参与考勤。</span>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【用户管理】-【权限】为所有用户分配权限。</span>
    <p>&nbsp;</p>
    <h5>我的菜单</h5>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【我的菜单】-【我的考勤】显示当前登录用户的所有打卡记录。</span>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【我的菜单】-【我的餐补】显示当前登录用户每天的上下班时间、餐补以及加班时间。</span>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【我的菜单】-【我的考勤异常】显示当前登录用户有哪些考勤异常以及处理情况。</span>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【我的菜单】-【我的统计】统计当前登录用户一段时间内总餐补、总加班时间、总调休时间和净加班时间。</span>
    <p>&nbsp;</p>
    <h5>统计查询</h5>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【统计查询】-【所有餐补】显示所有用户的所有打卡记录。</span>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【统计查询】-【所有考勤异常】显示所有用户有哪些考勤异常以及处理情况。</span>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【统计查询】-【所有统计】统计所有用户一段时间内总餐补、总加班时间、总调休时间和净加班时间。</span>
    <p>&nbsp;</p>
    <h5>后台管理</h5>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【后台管理】-【上传考勤记录】上传打卡记录。</span>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【后台管理】-【考勤异常表单】管理请假单，未打卡记录单等单据。</span>
    <p>&nbsp;</p>
    <h5>系统管理</h5>
    <span>&nbsp;&nbsp;&nbsp;&nbsp;【系统管理】-【考勤异常单据类型】编辑系统支持哪些单据，如年假、事假等（<font color="red" size="1">这里的数据一般只是系统初期设定，非特殊情况不做变更</font>）。 </span>
    <p>&nbsp;</p>
</form>
</body>
</html>
