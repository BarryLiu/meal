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
    <title>上传餐补信息</title>
    <link href="ligerUI/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" id="mylink"/>
    <script src="params.js" type="text/javascript"></script>
    <script src="ligerUI/lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
    <script src="js/jquery.utils.js" type="text/javascript"></script>
    <script src="js/jquery.form.js" type="text/javascript"></script>
    <script src="ligerUI/lib/jquery/jquery.utils.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerTextBox.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerCheckBox.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerMenu.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerComboBox.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerMenuBar.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerButton.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerResizable.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerDrag.js" type="text/javascript"></script>
    <script src="ligerUI/lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/ligerui.all.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerTab.js"></script>
    <script src="ligerUI/lib/jquery.cookie.js"></script>
    <script src="ligerUI/lib/json2.js"></script>
    <script type="text/javascript">
        checkParams = function () {
            var fileName = $("#file").val();
            if (fileName == undefined || fileName == "" || fileName == null) {
                $.ligerDialog.warn("请选择Excel文件！");
                return false;
            }
            return true;
        }
        upload = function () {
            if(checkParams()) {
                //$.ligerDialog.waitting("正在上传，请稍后...");
                alert($("#form1"));
                alert($("#form1").ajaxSubmit);
                $("#form1").ajaxSubmit({
                    type:"post",
                    dataType:"json",
                    url:"${pageContext.request.contextPath}/upload",
                    success:function(data) {
                        //$.ligerDialog.closeWaitting();
                        if(data.res) {
                            Tip.tip("上传成功！");
                            setTimeout(function() {
                                location.reload();
                            }, 1000);
                        } else {
                            Tip.tip(data.msg);
                        }
                    }
                });
            }
        }
        $(function() {
           alert($.ajaxSubmit);
        });
    </script>
    <style type="text/css">
        #form1 {
            margin-left:10px;
        }
    </style>
</head>
<body>
<form method="post" id="form1" action="upload" enctype="multipart/form-data">
    <p>&nbsp;</p>

    <h1>请选择餐补Excel文件</h1>

    <p>&nbsp;</p>
    <input type="file" name="file" id="file" accept=".xls">

    <p>&nbsp;</p>
    <input type="button" value="上传" name="上传" onclick="upload()">
    <br>
    <font color="red" font-size="14">${message}</font>
</form>
</body>
</html>
