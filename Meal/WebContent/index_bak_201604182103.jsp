<%--
  Created by IntelliJ IDEA.
  User: kui.li
  Date: 2014/9/2
  Time: 16:17
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<html>
<head>
    <title>Wheatek餐补系统——登陆</title>
    <link href="ligerUI/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css"/>
    <link href="ligerUI/lib/lib/ligerUI/skins/Gray/css/all.css" rel="stylesheet" type="text/css"/>
    <link href="ligerUI/lib/lib/ligerUI/skins/ligerui-icons.css" rel="stylesheet"/>
    <script src="ligerUI/lib/lib/jquery/jquery-1.3.2.min.js"></script>
    <script src="ligerUI/lib/lib/ligerUI/js/ligerui.all.js" type="text/javascript"></script>
    <script src="ligerUI/lib/lib/ligerUI/js/plugins/ligerForm.js"></script>
    <script src="ligerUI/lib/lib/json2.js" type="text/javascript"></script>
    <link href="style.css" rel="stylesheet"/>
    <style type="text/css">
        .middle input {
            display: block;
            width: 30px;
            margin: 2px;
        }
        #logindiv{
            height:200px;
            width:400px;
            position:absolute;
            top:50%;
            left:50%;
        }
    </style>
    <script type="text/javascript">
        function center()
        {
            var two=document.getElementById("logindiv");
            var two2=two.offsetHeight;
            var two3=two.offsetWidth;
            two.style.marginTop=-two2/2;
            two.style.marginLeft=-two3/2;
        }
    </script>
</head>
<body onload="center()">
<form action="login" id="form1" method="post">
    <div style="text-align:center;margin-right:auto;margin-left:auto;top:50%" id="logindiv">
        <h3>用户登录</h3>
        <br>
        <label><span>用户名：</span><input id="userName" name="userName" type="text"/></label>
        <label><span>密码：</span><input id="passWord" name="passWord" type="password"/></label>
        <label>
            <input name="" type="submit" class="bt" value="登陆"/>
            <font color="red">
                ${error_msg}
            </font>
        </label>
    </div>
</form>
</body>
</html>