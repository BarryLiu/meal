<%--
  Created by IntelliJ IDEA.
  User: kui.li
  Date: 2014/9/2
  Time: 15:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户同步</title>
    <script src='../dwr/util.js' type="text/javascript"></script>
    <script src='../dwr/engine.js' type="text/javascript"></script>
    <script src='../dwr/interface/dwrRole.js' type="text/javascript"></script>
    <link href="../ligerUI/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css"/>
    <script src="../params.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerTextBox.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerCheckBox.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerMenu.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerComboBox.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerMenuBar.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerButton.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerResizable.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
    <script src="../ligerUI/lib/ligerUI/js/plugins/ligerDrag.js" type="text/javascript"></script>
    <script src="../ligerUI/grid/CustomersData.js" type="text/javascript"></script>
    <script type="text/javascript">

        var gridManager = null;
        $(function () {
            //工具条
            $("#toptoolbar").ligerToolBar({ items: [
            ]
            });
            //表格
            $("#maingrid").ligerGrid({
                columns: [
                    { display: '登陆账号', name: 'login_count', isSort: 'true', align: 'left', minWidth: 120 },
                    { display: '姓名', name: 'name', isSort: 'true',  align: 'left',minWidth: 120 },
                    { display: '部门', name: 'depart', isSort: 'true',  align: 'left',minWidth: 120 },
                    { display: '同步者', name: 'create', isSort: 'true', width: 120, minWidth: 120 },
                    { display: '同步日期', name: 'date', isSort: 'true', width: 200, minWidth: 120 }
                ], dataAction: 'server', data: ${datas}, sortName: 'id',
                width: '100%', height: '100%', pageSize: 30, rownumbers: false,
                checkbox: false,
                //应用灰色表头
                cssClass: 'l-grid-gray',
                heightDiff: -6
            });

            gridManager = $("#maingrid").ligerGetGridManager();

            $("#pageloading").hide();
        });
    </script>
</head>
<body style="padding:0px; overflow:hidden;">
<div class="l-loading" style="display:block" id="pageloading"></div>

<form id="form1" action="/sync_user?op=other" method="post">
    <div id="toptoolbar"></div>
    <div class="l-panel-search">
        <div class="l-panel-search-item">
            ${userCounts}
        </div>
        <div class="l-panel-search-item">
            <input type="submit" value="同步数据"/><font color="red">将从LDAP系统同步数据</font>
        </div>
    </div>

    <div id="maingrid" style="margin:0; padding:0"></div>

</form>
<div style="display:none;">

</div>

</body>
</html>
