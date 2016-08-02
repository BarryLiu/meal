<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <script src='${pageContext.request.contextPath}/dwr/util.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/engine.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/interface/dwr.js' type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/params.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/cookie.js" type="text/javascript"></script>
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

    <script src="${pageContext.request.contextPath}/js/admin.js?t=201603172047" type="text/javascript"></script>



    <script type="text/javascript">
        $(function () {
            $("#maingrid").ligerGrid({
                columns: [
                    { display: '工号', name: 'number', isSort: 'true', width: 120, minWidth: 60},
                    { display: '姓名', name: 'name', isSort: 'true', width: 120, minWidth: 60},
                    { display: '一级部门', name: 'deptName1', isSort: 'true', width: 120, minWidth: 60},
                    { display: '二级部门', name: 'deptName2', isSort: 'true', width: 120, minWidth: 60},
                    { display: '日期', name: 'dayStr', isSort: 'true', width: 120, minWidth: 60},
                    { display: '异常开始时间', name: 'startTime', isSort: 'true', width: 120, minWidth: 60},
                    { display: '异常结束时间', name: 'endTime', isSort: 'true', width: 120, minWidth: 60},
                    { display: '异常总时长（小时）', name: 'totalHours', type: "int", isSort: 'true', width: 150, minWidth: 60},
                    { display: '异常类型', name: 'status', isSort: 'true', width: 120, minWidth: 60},
                    { display: '异常处理结果', name: 'billName', isSort: 'true', width: 120, minWidth: 60, render:function(item) {
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

            $("#number").chosen({
                search_contains:true
            });
            $("#startDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right',format: 'yyyy/MM/dd'});
            $("#endDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right',format: 'yyyy/MM/dd'});


            //yingjing.liu 20160726 start 根据部门查询回显
            $("#dept1").chosen({
                search_contains:true
            });
           /*  $("#dept2").chosen({
                search_contains:true
            });*/
            //yingjing.liu 20160726 end
        });
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

            /*var url="dept?op=other&fdeptid="+first;
            $.getJSON(url,function (data) {
               // alert(JSON.stringify(data));
                alert('into');
                $("#dept2").empty();
                $("#dept2").append($("<option/>").text('请选择').attr("value",''));
                //对请求返回的JSON格式进行分解加载
               /!* $(data).each(function () {
                    $("#dept2").append($("<option/>").text(this.name).attr("value",this.id));
                });*!/
            });*/
        }
     /*   function  firstChange(){
            var first= $("#firstDept").val();
            var url="dept?op=other&fdeptid="+first;
            $.getJSON(url,function (data) {
                $("#secondDept").empty();
                $("#secondDept").append($("<option/>").text('请选择').attr("value",''));
                //对请求返回的JSON格式进行分解加载
                $(data).each(function () {
                    $("#secondDept").append($("<option/>").text(this.name).attr("value",this.id));
                });
            });
        }*/
        deleteOverLeave = function(){
            $.ajax({
                type:"post",
                url:"${pageContext.request.contextPath}/leave",
                data:{"op":"delete"},
                dataType:"json",
                async:false,
                success:function(data) {
                    if(data.res) {
                        //Data.gridManager.deleteSelectedRow();
                        Tip.tip("删除成功！");
                        addCookie();
                        location.reload();
                    }
                },
                error:function() {
                    $.ligerDialog.error("Error!!!");
                }
            });
        }
        //  yingjing.liu 2016/07/21 cookie 保存 1s  页面 作用于 页面回显
        addCookie = function (){
            setCookie("currPage",gridManager.options.page,1);//在session中保存1秒钟
        }
        toPage = function(){
            var currPage = getCookie("currPage");
            if(currPage==""||currPage==null)return;
            gridManager.options.page=currPage-1;
            gridManager.changePage('next');
            removeCookie("currPage") //一次用完后删除
        }
        // yingjing.liu 2016/07/21 end
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
<body  style="padding:0px; overflow:hidden;"  onload="toPage()">
<div class="l-loading" style="display:block" id="pageloading"></div>
<form id="form1" action="leave" method="post">
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

        <div class="l-panel-search-item" style="margin-left: 20px;">
            <input type="button" onclick="deleteOverLeave()" value="删除异常"/>
        </div>

    </div>

    <div id="maingrid" style="margin:0; padding:0;"></div>

</form>

<div style="display:none;">
</div>

</body>
</html>
