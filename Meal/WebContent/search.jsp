<%--
  Created by IntelliJ IDEA.
  User: kui.li
  Date: 2014/9/2
  Time: 15:44
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>统计查询</title>


    <script src='${pageContext.request.contextPath}/dwr/util.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/engine.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/interface/dwr.js' type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/params.js" type="text/javascript"></script>

    <script src="${pageContext.request.contextPath}/js/cookie.js" type="text/javascript"></script>

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

    <!-- chosen -->
    <link href="${pageContext.request.contextPath }/js/chosen/chosen.min.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath }/js/chosen/chosen.jquery.min.js"></script>

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
                    { display: '工号|姓名', name: 'user_info', isSort: 'true', width: 120, minWidth: 60 },
                    { display: '日期', name: 'day_str', isSort: 'true', width: 120, minWidth: 60 , render: function (rowdata, rowindex, value)
                    {
                        var h = "<a href='javascript:viewKaoqin(\"" + rowdata.day_str + "\",\"" + rowdata.user_info + "\")'>" + rowdata.day_str + "</a> ";
                        return h;
                    }},
                    { display: '一级部门', name: 'deptName1', isSort: 'true', width: 120, minWidth: 60},
                    { display: '二级部门', name: 'deptName2', isSort: 'true', width: 120, minWidth: 60},
                    { display: '上班时间', name: 'start_time',align: 'center', isSort: 'true', width: 80, minWidth: 60 },
                    { display: '下班时间', name: 'end_time',align: 'center', isSort: 'true', width: 120, minWidth: 60 },
                    { display: '加班时间', name: 'workovertime',align: 'left', isSort: 'true', width: 120, minWidth: 60 },
                    { display: '早餐', name: 'fee1',align: 'left', isSort: 'true', width: 70, minWidth: 60 },
                    { display: '午餐', name: 'fee2',align: 'left', isSort: 'true', width: 70, minWidth: 60 },
                    { display: '晚餐', name: 'fee3', align: 'left',isSort: 'true', width: 70, minWidth: 60 },
                    { display: '宵夜1', name: 'fee4',align: 'left', isSort: 'true', width: 70, minWidth: 60 },
                   // { display: '宵夜2', name: 'fee5', align: 'left',isSort: 'true', width: 100, minWidth: 60 },
                    { display: '合计', name: 'total', align: 'left',isSort: 'true', width: 100, minWidth: 60 },
                    { display: '状态', name: 'status', align: 'center',isSort: 'true', width: 80, minWidth: 60 },
                    { display: '处理结果', name: 'dealStatus', align: 'center',isSort: 'true', width: 150, minWidth: 60 }
                ], dataAction: 'server', data: ${data},
                width: '100%', height: '100%', pageSize: 30, rownumbers: false,
                checkbox: false,
                //应用灰色表头
                cssClass: 'l-grid-gray',
                heightDiff: -6
            });

            gridManager = $("#maingrid").ligerGetGridManager();

            //yingjing.liu 20160726 start
            /* $("#dept1").chosen({
                search_contains:true
            });*/
            //yingjing.liu 20160726 end

            $("#pageloading").hide();

            $("#sDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});
            $("#eDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});

            if("${download}" == "1") {
                $("#outTag").val("2");
                $("#form1").submit();
            }
        });

        output = function () {
            $("#outTag").val("1");
            return true;
        }
        search = function() {
            $("#outTag").val("");
            return true;
        }
        viewKaoqin = function (day,name) {
            if(day == '') {
                $.ligerDialog.warn('参数不合法！！');
                return;
            }
            parent.tab.addTabItem({
                tabid: 1234,
                text: '考勤明细',
                url: 'kaoqin?sDate=' + day + '&eDate=' + day + '&sUserName=' + encodeURI(encodeURI(name))
            });
        }
        // yingjing.liu 20160726 add start 二级联动
        function  firstChange(){
            var first= $("#dept1").val();
            if(first==''||first=='-1'){
                $("#secondDept").empty();
                return
            }
            var i=100;
            dwr.getDeptSecond(first,function(data) {
                var res = eval("("+data+")");
                if(i==100){
                    $("#dept2").empty();
                    $("#dept2").append($("<option/>").text('请选择').attr("value",''));
                    //对请求返回的JSON格式进行分解加载
                    $(res).each(function () {
                        $("#dept2").append($("<option/>").text(this.name).attr("value",this.id));
                    });
                    i=0;
                }
            });
        }
        // yingjing.liu 20160726 add end
    </script>
</head>
<body style="padding:0px; overflow:hidden;">
<div class="l-loading" style="display:block" id="pageloading"></div>
<form id="form1" action="output" method="post">

    <input type="hidden" id="outTag" name="outTag" value="${outTag}"/>
    <div id="toptoolbar"></div>
    <div class="l-panel-search">
        <div class="l-panel-search-item">
            姓名|工号：
        </div>
        <div class="l-panel-search-item">
            <input type="text" id="name" name="name" value="${name}"/>
        </div>
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

        <%--yingjing.liu 20160726 add start 添加对 部门的查询操作下拉框--%>
        <div class="l-panel-search-item">
            一级部门：
        </div>
        <div class="l-panel-search-item">
            <select id="dept1" name="dept1" onchange="firstChange()">
                <option value="">全部1</option>
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
        <%--yingjing.liu 20160726 add end--%>

        <div class="l-panel-search-item">
            <input type="submit" value="搜 索" onclick="return search();"/>
            <c:if test="${empty sessionScope.manageMentUser}"> <%--不是部门管理人并且有权限才可以导出--%>
                <input type="submit" onclick="return output();" value="搜索&导出Excel"/>
            </c:if>
        </div>
    </div>

    <div id="maingrid" style="margin:0; padding:0"></div>

</form>
<div style="display:none;" id="add">
</div>

</body>
</html>
