<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <script src='${pageContext.request.contextPath}/dwr/util.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/engine.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/interface/dwr.js' type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/params.js" type="text/javascript"></script>

    <link href="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/ligerui-icons.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/Gray/css/all.css" rel="stylesheet" type="text/css" />
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/jquery/jquery-1.9.0.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.form.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerGrid.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerToolBar.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerResizable.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerForm.js?t=201603212002" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerCheckBox.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerTextBox.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerComboBox.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerButton.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerRadio.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerTip.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerDateEditor.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/js/plugins/ligerSpinner.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/json2.js" type="text/javascript"></script>

    <script src="${pageContext.request.contextPath}/js/admin.js?t=201603172046" type="text/javascript"></script>

    <script type="text/javascript">
        $(function () {
            $("#maingrid").ligerGrid({
                columns: [
                    { display: '工号', name: 'number', isSort: 'true', width: 150, minWidth: 60},
                    { display: '姓名', name: 'name', isSort: 'true', width: 150, minWidth: 60},
                    { display: '日期', name: 'dayStr', isSort: 'true', width: 150, minWidth: 60},
                    { display: '异常开始时间', name: 'startTime', isSort: 'true', width: 150, minWidth: 60},
                    { display: '异常结束时间', name: 'endTime', isSort: 'true', width: 150, minWidth: 60},
                    { display: '异常总时长（小时）', name: 'totalHours', type: "int", isSort: 'true', width: 150, minWidth: 60},
                    { display: '异常类型', name: 'status', isSort: 'true', width: 150, minWidth: 60},
                    { display: '异常处理结果', name: 'billName', isSort: 'true', width: 150, minWidth: 60, render: function(item) {
                        if(item.billName == null || item.billName == "" || item.billName == "无") {
                            return "<font color='red'>未处理</font>";
                        }
                        return item.billName;
                    }}
                ], dataAction: 'server', data: ${data},enabledEdit:false,
                width: '100%', height: '100%', pageSize: 20, rownumbers: false,
                checkbox: false,
                //应用灰色表头
                cssClass: 'l-grid-gray',
                heightDiff: -40
            });
            gridManager = $("#maingrid").ligerGetGridManager();
            $("#pageloading").hide();

            $("#startDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right',format: 'yyyy/MM/dd'});
            $("#endDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right',format: 'yyyy/MM/dd'});

        });
    </script>
    <style type="text/css">
        .l-panel-search{
            padding-bottom: 2px;;
        }
        .chosen-container{
            font-size:8px;
        }
        .chosen-container-single .chosen-single{
            height:22px;
            line-height:20px;
        }
    </style>
</head>
<body  style="padding:0px; overflow:hidden;">
<div class="l-loading" style="display:block" id="pageloading"></div>
<form id="form1" action="myleave" method="post">
    <div id="toptoolbar"></div>
    <div class="l-panel-search">
        <div class="l-panel-search-item">
            开始日期：
        </div>
        <div class="l-panel-search-item">
            <input type="text" id="startDate" name="startDate" value="${startDate}"/>
        </div>
        <div class="l-panel-search-item">
            结束日期：
        </div>
        <div class="l-panel-search-item">
            <input type="text" id="endDate" name="endDate" value="${endDate}"/>
        </div>
        <div class="l-panel-search-item">
            <input type="submit" value="搜 索"/>
        </div>
    </div>

    <div id="maingrid" style="margin:0; padding:0;"></div>

</form>

<div style="display:none;">
</div>

</body>
</html>
