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
    <!-- chosen -->
    <link href="${pageContext.request.contextPath }/js/chosen/chosen.min.css?t=201603283" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath }/js/chosen/chosen.jquery.min.js"></script>

    <script src="${pageContext.request.contextPath}/js/admin.js?t=201603172046" type="text/javascript"></script>

    <script type="text/javascript">
        $(function () {
            $("#maingrid").ligerGrid({
                columns: [
                    { display: '工号', name: 'number', isSort: 'true', width: 120, minWidth: 60},
                    { display: '姓名', name: 'name', isSort: 'true', width: 120, minWidth: 60},
                    { display: '一级部门', name: 'deptName1', isSort: 'true', width: 120, minWidth: 60},
                    { display: '二级部门', name: 'deptName2', isSort: 'true', width: 120, minWidth: 60},
                    { display: '总餐补', name: 'totalfees', type: "int", isSort: 'true', width: 120, minWidth: 60},
                    { display: '总加班时间（分钟）', name: 'totalovertimes', type: "int", isSort: 'true', width: 120, minWidth: 60},
                    { display: '总调休时间（分钟）', name: 'totalleaves', type: "int", isSort: 'true', width: 120, minWidth: 60},
                    { display: '净加班时间（分钟）', name: 'actualovertimes', type: "int", isSort: 'true', width: 120, minWidth: 60},
                    { display: '总未处理时间（分钟）', name: 'unhandledleaves', type: "int", isSort: 'true', width: 150, minWidth: 60, render: function(item) {
                        return "<span style='color:#FF0000;'>"+item.unhandledleaves+"</span>"
                    }}
                ], dataAction: 'server', data: ${data},enabledEdit:false,
                width: '100%', height: '100%', pageSize: 20, rownumbers: false,
                checkbox: false,
                //应用灰色表头
                cssClass: 'l-grid-gray',
                heightDiff: -44
            });

            gridManager = $("#maingrid").ligerGetGridManager();
            $("#pageloading").hide();

            $("#number").chosen({
                search_contains:true
            });

            $("#dept1").chosen({
                search_contains:true
            });

            $("#startDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right',format: 'yyyy/MM/dd'});
            $("#endDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right',format: 'yyyy/MM/dd'});

        });

        function  firstChange() {

            var first = $("#dept1").val();
            if(first==''||first=='-1'){
                $("#secondDept").empty();
                return
            }
            var i = 100;
            dwr.getDeptSecond(first, function (data) {
                var res = eval("(" + data + ")");
                if (i == 100) {
                    $("#dept2").empty();
                    $("#dept2").append($("<option/>").text('请选择').attr("value", ''));
                    //对请求返回的JSON格式进行分解加载
                    $(res).each(function () {
                        $("#dept2").append($("<option/>").text(this.name).attr("value", this.id));
                    });
                    i = 0;
                }
            });
        }
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
<form id="form1" action="statistic" method="post">
    <div id="toptoolbar"></div>
    <div class="l-panel-search">
        <div class="l-panel-search-item">
            员工：
        </div>
        <div class="l-panel-search-item">
            <select id="number" name="number">
                <option value="">全部</option>
                <c:forEach items="${tUsers}" var="tUser">
                    <option value="${tUser.number}" <c:if test="${tUser.number eq number}">selected="selected"</c:if>>${tUser.displayName}</option>
                </c:forEach>
            </select>
        </div>
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
            一级部门：
        </div>
        <div class="l-panel-search-item">
            <select id="dept1" name="dept1" onchange="firstChange()">
                <option value="">全部</option>
                <c:forEach items="${depts}" var="d">
                    <option value="${d.id}" <c:if test="${d.id eq dept1}">selected="selected"</c:if>>${d.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="l-panel-search-item">
            二级部门：
        </div>
        <div class="l-panel-search-item">
            <select id="dept2" name="dept2">
                <option value="">全部</option>
                <c:forEach items="${deptSeconds}" var="d">
                    <option value="${d.id}" <c:if test="${d.id eq dept2}">selected="selected"</c:if>>${d.name}</option>
                </c:forEach>
            </select>
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
