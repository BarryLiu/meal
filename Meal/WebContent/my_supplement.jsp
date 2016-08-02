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
    <title>我的餐补</title>

    <script src='dwr/util.js' type="text/javascript"></script>
    <script src='dwr/engine.js' type="text/javascript"></script>
    <link href="ligerUI/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css"/>
    <script src="params.js" type="text/javascript"></script>
    <script src="ligerUI/lib/jquery/jquery-1.3.2.min.js" type="text/javascript"></script>
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
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerDateEditor.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerTab.js" type="text/javascript"></script>

    <script type="text/javascript">

        var editID = "";

        var alert = function (content) {
            $.ligerDialog.alert(content);
        };
        var gridManager = null;
        $(function () {
            //表格
//            var data = {Rows:[{user_info:'210000010-蔡正龙',day_str:'2014-10-15',start_time:'09:26:54',end_time:'21:05:28',fee1:'null',fee2:'12',fee3:'15',fee4:'null',fee5:'null'}]};
            $("#maingrid").ligerGrid({
                columns: [
                    { display: '工号|姓名', name: 'user_info', isSort: 'true', width: 150, minWidth: 60 },
                    { display: '日期', name: 'day_str', isSort: 'true', width: 180, minWidth: 60 , render: function (rowdata, rowindex, value)
                    {
                        var h = "<a href='javascript:viewKaoqin(\"" + rowdata.day_str + "\",\"" + rowdata.user_info + "\")'>" + rowdata.day_str + "</a> ";
                        return h;
                    }},
                    { display: '上班时间', name: 'start_time',align: 'center', isSort: 'true', width: 180, minWidth: 60 },
                    { display: '下班时间', name: 'end_time',align: 'center', isSort: 'true', width: 150, minWidth: 60 },
                    { display: '加班时间', name: 'workovertime',align: 'left', isSort: 'true', width: 100, minWidth: 60 },
                    { display: '早餐', name: 'fee1',align: 'left', isSort: 'true', width: 100, minWidth: 60 },
                    { display: '午餐', name: 'fee2',align: 'left', isSort: 'true', width: 90, minWidth: 60 },
                    { display: '晚餐', name: 'fee3', align: 'left',isSort: 'true', width: 90, minWidth: 60 },
                    { display: '宵夜1', name: 'fee4',align: 'left', isSort: 'true', width: 90, minWidth: 60 },
                   // { display: '宵夜2', name: 'fee5', align: 'left',isSort: 'true', width: 100, minWidth: 60 },
                    { display: '合计', name: 'total', align: 'left',isSort: 'true', width: 100, minWidth: 60 },
                    { display: '状态', name: 'status', align: 'center',isSort: 'true', width: 80, minWidth: 60 },
                    { display: '处理结果', name: 'dealStatus', align: 'center',isSort: 'true', width: 150, minWidth: 60 }
                ], dataAction: 'server', data: ${data},
                width: '100%', height: '100%', pageSize: 20, rownumbers: false,
                checkbox: false,
                //应用灰色表头
                cssClass: 'l-grid-gray',
                heightDiff: -6
            });

            gridManager = $("#maingrid").ligerGetGridManager();

            $("#pageloading").hide();

            $("#sDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});
            $("#eDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});
        });
        viewKaoqin = function (day,name) {
            if(day == '') {
                $.ligerDialog.warn('参数不合法！！');
                return;
            }
            parent.tab.addTabItem({
                tabid: 1235,
                text: '我的考勤明细',
                url: 'kaoqin?sDate=' + day + '&eDate=' + day + '&sUserName=' + encodeURI(encodeURI(name))
            });
        }
    </script>
</head>
<body style="padding:0px; overflow:hidden;">
<div class="l-loading" style="display:block" id="pageloading"></div>
<form id="form1" action="mySupplement" method="post">

    <div id="toptoolbar"></div>
    <div class="l-panel-search">
        <div class="l-panel-search-item">
            开始日期：
        </div>
        <div class="l-panel-search-item">
            <input type="text" id="sDate" name="sDate" value="${sDate}"/>
        </div>
        <div class="l-panel-search-item">
            结束日期：
        </div>
        <div class="l-panel-search-item">
            <input type="text" id="eDate" name="eDate" value="${eDate}"/>
        </div>
        <div class="l-panel-search-item">
            <input type="submit" value="搜 索"/>
        </div>
    </div>

    <div id="maingrid" style="margin:0; padding:0"></div>

</form>
<div style="display:none;">

</div>

</body>
</html>
