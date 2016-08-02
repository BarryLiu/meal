<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <script src='${pageContext.request.contextPath}/dwr/util.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/engine.js' type="text/javascript"></script>
    <script src='${pageContext.request.contextPath}/dwr/interface/dwr.js' type="text/javascript"></script>
    <link href="ligerUI/lib/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css"/>
    <script src="js/jquery-1.7.2.min.js" type="text/javascript"></script>
    <script src="js/jquery.utils.js" type="text/javascript"></script>
    <script src="js/jquery.form.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/core/base.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/ligerui.all.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerDateEditor.js" type="text/javascript"></script>
    <script src="ligerUI/lib/ligerUI/js/plugins/ligerDialog.js" type="text/javascript"></script>

    <script type="text/javascript">
        checkParams = function () {
            var fileName = $("#file").val();
            if (fileName == undefined || fileName == "" || fileName == null) {
                $.ligerDialog.warn("请选择Excel文件！");
                return false;
            }
            return true;
        };
        upload = function () {
            if(checkParams()) {
                $.ligerDialog.waitting("正在上传，需时间较长，请耐心等候...");
                $("#form1").ajaxSubmit({
                    type:"post",
                    dataType:"json",
                    url:"${pageContext.request.contextPath}/upload",
                    success:function(data) {
                        $.ligerDialog.closeWaitting();
                        if(data.res) {
                            $.ligerDialog.success("上传成功！","提示", function() {
                                location.reload();
                            });
                        } else {
                            $.ligerDialog.error(data.msg);
                        }
                    }
                });
            }
        };

        sendEmail = function() {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            if(startDate == '') {
                $.ligerDialog.warn("开始日期不能为空！");
                return;
            }
            if(endDate == '') {
                $.ligerDialog.warn("结束日期不能为空！");
                return;
            }
            $.ligerDialog.waitting("正在发送邮件，请稍后...");
            dwr.notifyAttendance(startDate, endDate, function(data) {
                $.ligerDialog.closeWaitting();
                var res = eval('('+data+')');
                if(res.res) {
                    $.ligerDialog.success("邮件已发送成功！","提示",function() {
                        location.reload();
                    });
                } else {
                    $.ligerDialog.error("邮件发送失败，请联系系统管理员！");
                }
            });
        };


        exportbyPoi = function() {
            var startDate = $("#tostartDate").val();
            var endDate = $("#toendDate").val();
            if(startDate == '') {
                $.ligerDialog.warn("开始日期不能为空！");
                return;
            }
            if(endDate == '') {
                $.ligerDialog.warn("结束日期不能为空！");
                return;
            }
            //$.ligerDialog.alert("导出中,请等待...");
            //alert("提交了");
            document.getElementById("form3").submit();
            // alert("submited");
         /*   var str = "${pageContext.request.contextPath}/export";//?startDay="+startDate+"&endDay="+endDate;
            alert(str);
           */

           /* $.ligerDialog.waitting("正在导出，请稍后...");
            dwr.exportPoi(startDate, endDate, function(data) {
                $.ligerDialog.closeWaitting();
                var res = eval('('+data+')');
                if(res.res) {
                    $.ligerDialog.success("导出成功！已经保存到D盘根目录。","提示",function() {
                        location.reload();
                    });
                } else {
                    $.ligerDialog.error("导出失败，请联系系统管理员！");
                }
            });*/


        };


        uploadDesc = function () {
            $.ligerDialog.alert($("#uploadDescContent").html(),"上传考勤记录说明",'question',function() {
            },{width:600, isResize:true, showMax:true});
        };
        $(function() {
            $("#sDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});
            $("#eDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});
            $("#startDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});
            $("#endDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});

            $("#tostartDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});
            $("#toendDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});




            $("#ckb_all").click(function(){
                if($("#ckb_all").is(':checked')){
                    $("[type='checkbox']").attr("checked",'true');//全选
                }else{
                    $("[type='checkbox']").removeAttr("checked");//取消全选
                }
            });
        });


        function  firstChange() {
            var first = $("#firstDept").val();

            if (first == '' || first == '-1') {
                $("#secondDept").empty();
                $("#secondDept").append($("<option/>").text('请选择').attr("value", ''));
                return
            }
            var i = 100;
            dwr.getDeptSecond(first, function (data) {
                var res = eval("(" + data + ")");
                if (i == 100) {
                    $("#secondDept").empty();
                    $("#secondDept").append($("<option/>").text('请选择').attr("value", ''));
                    //对请求返回的JSON格式进行分解加载
                    $(res).each(function () {
                        $("#secondDept").append($("<option/>").text(this.name).attr("value", this.id));
                    });
                    i = 0;
                }
            });
        }
    </script>
    <style type="text/css">
        form {
            margin-left:10px;
        }
        form h1{
            padding: 15px 0px 15px 10px;
        }
        form input {
            margin-bottom:10px;
            margin-left: 10px;
        }
        div.date-wrap {
            width: 100%;
            min-width: 500px;
            height: 30px;
        }
        div.date-wrap div {
            float: left;
        }
        div.date-wrap div.clear-right {
            clear: right;
        }
        div.date-wrap div span {
            color: #FF0000;
            display: block;
            margin-left: 3px;
            margin-top:2px;
            font-size: 15px;
        }
    </style>
</head>
<body>
<br/>
<span style="display: none;" id="uploadDescContent">
    <div style="margin-left:10px;margin-right: 2px;">
    开始时间和结束时间说明：<br/>
    <font color="#d2691e">作用：避免考勤系统中最后的一天出现很多不正确的考勤异常。</font><br/>
    考勤的完整一天是从当天的06:01:00开始到第二天的06:00:59结束，例如2016/05/05的考勤时间就是：2016/05/05 06:01:00 - 2016/05/06 06:00:59。<br/>
    上面的开始日期，结束日期同样指的是考勤的完整天，而不是普通0点到24点的一天。<br/>
    例如：开始日期2016/05/01，结束日期选择2016/05/03，那么会录入 2016/05/01 06:01:00 - <font color="red">2016/05/04</font> 06:00:59 的考勤数据。<br />
    开始时间和结束时间只是限制了此次录入的最大时间范围，而最终实际录入情况还跟Excel中的实际数据有关。<br/>
    比如选择录入范围为2016/05/01-2016/05/05,而如果Excel中实际是2016/05/04-2016/05/08的考勤数据，那么最终会录入：2016/05/04 00:00:00 - <font color="red">2016/05/06</font> 06:00:59的考勤。<br />
    <font color="red">开始时间和结束时间均可为空，为空表示不限制，正常情况下只选择结束时间即可。</font><br/>
    </div>
</span>
<form method="post" id="form1" action="upload" enctype="multipart/form-data">
    <h1>请选择餐补Excel文件</h1>
    <input type="file" name="file" id="file" accept=".xls">
    <br/>
    <div class="date-wrap">
    <div>开始日期：</div><div class="clear-right"><input type="text" id="sDate" name="sDate" value="${sDate}"/></div>
    </div>
    <div class="date-wrap">
    <div>结束日期：</div><div class="clear-right"><input type="text" id="eDate" name="eDate" value="${eDate}"/></div>
    </div>
    <br/>
    <input type="button" value="上传" name="uploadBtn" onclick="upload();" /><input type="button" value="说明" name="descBtn" onclick="uploadDesc()" />
    <br/>
    <font color="red" font-size="14">${message}</font>
</form>
<br/>
<form method="post" id="form2" action="upload">
    <h1>发送考勤异常通知邮件</h1>
    <div class="date-wrap">
        <div>开始日期：</div><div><input type="text" id="startDate" name="sDate" value="${sDate}"/></div><div><span class="clear-right">*</span></div>
    </div>
    <div class="date-wrap">
        <div>结束日期：</div><div><input type="text" id="endDate" name="eDate" value="${eDate}"/></div><div><span class="clear-right">*</span></div>
    </div>
    <br/>
    <input type="button" value="发送邮件" name="sendEmial" onclick="sendEmail();" />






</form>
<br /><br />

<div>
<form  action="export" id="form3">
    <%--<input type="checkbox" checked="checked" id="ckb_all" >全选/全不选<br/>
    <c:set var="i" value="0"></c:set>
    &lt;%&ndash;    全局变量i让其需要 迭代换行  &ndash;%&gt;
    <table>
        <tr>
    <c:forEach var="g" items="${groups}">
            <td>
               <input type="checkbox" checked="checked" name="dept" value="${g.id}"> ${g.groupName}
            </td>
              <c:set var="i" value="${i+1 }"></c:set>
              <c:if test="${i%5==0 }"></tr><tr></c:if>
    </c:forEach>
    </tr>
    </table>--%>

    <table >
        <tr>
            <td colspan="2">
                <h5 style="color:red">导出规则:<br/>有选二级部门就依二级部门,只选一级部门就以一级部门,都不选依全部导出</h5>
            </td>
        </tr>
        <tr  id="div_type1" >
            <td style="align:right" width="80px">一级部门:</td>
            <td>
                <select id="firstDept" name="parentId" onchange="firstChange()">
                    <option value="" >请选择部门</option>
                    <c:forEach items="${depts}" var="dept">
                        <option value="${dept.id}" >${dept.name}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <td style="align:right" width="80px">二级部门:</td>
            <td>
                <select id="secondDept" name="secondDept" style="width: 120px;">
                    <option value="">请选择</option>
                </select>
            </td>
        </tr>
    </table>
    <h1>导出考勤情况报表</h1>
    <div class="date-wrap">
        <div>开始日期：</div><div><input type="text" id="tostartDate" name="startDay" value="${sDate}"/></div><div><span class="clear-right">*</span></div>
    </div>
    <div class="date-wrap">
        <div>结束日期：</div><div><input type="text" id="toendDate" name="endDay" value="${eDate}"/></div><div><span class="clear-right">*</span></div>
    </div>
    <br/>

   <%-- <input type="submit" value="form导出" >--%>
    <input type="button" value="导出" onclick="exportbyPoi()">

</form>
</div>
<br /><br />



</body>
</html>
