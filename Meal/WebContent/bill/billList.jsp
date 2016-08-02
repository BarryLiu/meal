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
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/jquery-validation/jquery.metadata.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/jquery-validation/jquery.validate.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/jquery-validation/messages_cn.js" type="text/javascript"></script>
    <!-- chosen -->
    <link href="${pageContext.request.contextPath }/js/chosen/chosen.min.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath }/js/chosen/chosen.jquery.min.js"></script>

    <script src="${pageContext.request.contextPath}/js/admin.js?t=201603172046" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/modify.validate.common.js?t=2016032316491" type="text/javascript"></script>

    <style type="text/css">
        body{ font-size:12px;}
        .l-table-edit {}
        .l-table-edit-td{ padding:4px;}
        .l-button-submit,.l-button-test{width:80px; float:left; margin-left:10px; padding-bottom:2px;}
        .l-verify-tip{ left:230px; top:120px;}
        select#number {
            min-width:200px;
        }
        select#billName {
            min-width:200px;
        }
        #maingrid table td span.hyperlink{
            color:#395a7b;
            text-decoration:none;
        }
        #maingrid table td span.hyperlink:hover {
            text-decoration:underline;
        }
    </style>

    <script type="text/javascript">
        $(function () {

            var billNameData = eval("(${billNameData})");
            $("#maingrid").ligerGrid({
                columns: [
                    { display: '工号', name: 'number', isSort: 'true', width: 120, minWidth: 60},
                    { display: '姓名', name: 'name', isSort: 'true', width: 120, minWidth: 60},
                    { display: '一级部门', name: 'deptName1', isSort: 'true', width: 120, minWidth: 60},
                    { display: '二级部门', name: 'deptName2', isSort: 'true', width: 120, minWidth: 60},
                    { display: '单据开始时间', name: 'startDate', isSort: 'true', width: 130, minWidth: 60},
                    { display: '单据结束时间', name: 'endDate', isSort: 'true', width: 130, minWidth: 60},
                    { display: '单据总时长（小时）', name: 'totalHours', isSort: 'true', width: 120, minWidth: 60},
                    { display: '单据名称', name: 'billName', isSort: 'true', width: 150, minWidth: 60, render:
                            function(item) {
                                return "<span class='hyperlink'>"+item.billName+"</span>";
                            },editor:{
                                type:'select',data:billNameData, valueField:'billName', selectBoxHeight : 200,textField:'billName',onChange:
                                    function(item){
                                        var before = item.record.billName;
                                        var after = item.value;
                                        if(after == '') {
                                            item.value = before; //数据回退
                                        } else if(before != after) {
                                            var id = item.record.id;
                                            $.ajax({
                                               type:"post",
                                                url:"${pageContext.request.contextPath}/bill",
                                                data:{"op":"update","id":id,"billName":after},
                                                dataType:"json",
                                                async:false,
                                                success:function(data) {
                                                    if(data.res) {
                                                        Tip.tip("修改成功！");

                                                        addCookie();
                                                        reload();
                                                    } else {
                                                        Tip.tip("修改失败");
                                                        item.value = before;
                                                    }
                                                }
                                            });
                                        }
                                        return true;
                                    }
                            }
                    },
                    { display: '说明', name: 'descs', isSort: 'true', width: 250, minWidth: 60}
                ], dataAction: 'server', data: ${data},enabledEdit:true,
                width: '100%', height: '100%', pageSize: 20, rownumbers: false,
                checkbox: true,
                //应用灰色表头
                cssClass: 'l-grid-gray',
                resizable:false,
                alternatingRow:true,
                heightDiff: -22,
                toolbar: { items: [
                    { text: '增加', click: addItemClick, icon: 'add' },
                    /*{ line: true },
                    { text: '修改', click: modifyItemClick, icon: 'modify' },*/
                    { line: true },
                    { text: '删除', click: deleteItemClick, img: '${pageContext.request.contextPath}/LigerUI_V1.3.3/lib/ligerUI/skins/icons/delete.gif' }
                ]
                }
            });

            Data.gridManager = $("#maingrid").ligerGetGridManager();

            $("#pageloading").hide();

            Data.dialogWidth = 600;
            Data.dialogHeight = 320;

            $("#number1").chosen({
                search_contains:true
            });
            $("#billName1").chosen({
                search_contains:true
            });
            $("#billName3").chosen({
                search_contains:true
            });
            $("#sDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right', format: 'yyyy/MM/dd'});
            $("#dept1").chosen({
                search_contains:true
            });



        });

        Data.afterOpenDialog = function() {
            $("#form2").ligerForm();
             $("#number").chosen({
                search_contains:true
             });
             $("#billName").chosen({
                search_contains:true
             });
             Data.numberDiv = $("#number").next("div").eq(0);
             Data.billNamediv = $("#billName").next("div").eq(0);
             $("#number").change(function() {
                 if($(this).val() != '') {
                     Data.numberDiv.ligerHideTip();
                 } else {
                     Data.numberDiv.ligerTip({content:"请选择员工！"});
                 }
             });
            $("#billName").change(function() {
                if($(this).val() != '') {
                    Data.billNamediv.ligerHideTip();
                }
            });
        };
        Data.resetData = function() {
            $("#form2")[0].reset();
            //var form2 = liger.get("form2");
            //form2.setData({"pms.billType":"0"}); //目前系統有個限制：值不能為false，比如空字符串。數字0，變量false等。具體限制在ligerForm.js的setData方法中
            Data.numberDiv.ligerHideTip();
            Data.billNamediv.ligerHideTip();
            $("#number").chosen("destroy");
            $("#billName").chosen("destroy");
            $("#number").chosen({
                search_contains:true
            });
            $("#billName").chosen({
                search_contains:true
            });
            // yingjing.liu  add  20160721 start 批量处理
            $("#billName3").chosen({
                search_contains:true
            });

            // yingjing.liu  add  20160721 end
            Data.numberDiv = $("#number").next("div").eq(0);
            Data.billNamediv = $("#billName").next("div").eq(0);
        }

        Data.validate = function() {
            var res = true;
            var number = $("#number").val();
            var billName = $("#billName").val();
            if(number == '') {
                Data.numberDiv.ligerTip({content:"请选择员工！"});
                res = false;
            }
            if(billName == '') {
                Data.billNamediv.ligerTip({content:"请选择单据名称！"});
                res = false;
            }
            return res;
        }
        Data.submitHandler = function(form) {
            $("#form2").ajaxSubmit({
                type:"post",
                url:"${pageContext.request.contextPath}/bill",
                dataType:"json",
                success:function(data) {
                    if(data.res) {
                        Tip.tip("添加成功！");
                        Data.resetData();
                        Data.dialog.hide();
                        //Data.gridManager.addRow(data.data,Data.gridManager.getRow(0), true);
                        addCookie();
                        reload();
                    } else {
                        $.ligerDialog.error(data.msg);
                        //$("#billName").ligerTip({content:data.msg, target:this});
                        //Tip.tip(data.msg);
                    }
                }
            });
        };

        function deleteItemClick(item) {
            var rows = Data.gridManager.getSelectedRows();
            if(rows != '') {
                $.ligerDialog.confirm("The operation can't be restored,are you sure to delete the selected items?", function(type) {
                    if(type) {
                        var da = "op=delete";
                        for(var i=0;i<rows.length;i++) {
                            da += "&ids="+rows[i].id;
                        }
                        $.ajax({
                            type:"post",
                            url:"${pageContext.request.contextPath}/bill",
                            data:da,
                            dataType:"json",
                            async:false,
                            success:function(data) {
                                if(data.res) {
                                    //Data.gridManager.deleteSelectedRow();
                                    Tip.tip("删除成功！");
                                    addCookie();
                                    reload();
                                }
                            },
                            error:function() {
                                $.ligerDialog.error("Error!!!");
                            }
                        });
                    }
                });
            } else {
                $.ligerDialog.alert("please select one item at least!");
            }
        }
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
        function reload() {
            setTimeout(function() {
                location.reload();
            }, 1000)
        }

        //  yingjing.liu 2016/06/14 cookie 保存 1s  页面 作用于 页面回显
        addCookie = function (){
            setCookie("currPage",Data.gridManager.options.page,1);//在session中保存1秒钟
        }
        toPage = function(){
            var currPage = getCookie("currPage");
            if(currPage==""||currPage==null)return;
            Data.gridManager.options.page=currPage-1;
            Data.gridManager.changePage('next');
            removeCookie("currPage") //一次用完后删除
        }
        // yingjing.liu 2016/06/14 end
        <%--yingjing.liu  20160721 start add 添加一个复选框便于做批量调休 --%>
        changeStatus = function (){
            var rows = Data.gridManager.getSelectedRows();

            var billVal  = document.getElementById("billName3").value;
            if(billVal==''){
                $.ligerDialog.alert("先选择改为什么状态");
                return;
            }
            if(rows != '') {
                for(var i=0;i<rows.length;i++) {
                   var id = rows[i].id;
                    $.ajax({
                        type:"post",
                        url:"${pageContext.request.contextPath}/bill",
                        data:{"op":"update","id":id,"billName":billVal},
                        dataType:"json",
                        async:false,
                        success:function(data) {
                        }
                    });
                }
                    Tip.tip("修改成功！");
                    addCookie();
                    reload();
          } else {
            $.ligerDialog.alert("选择要改的人");
         }
        }
        <%--yingjing.liu  20160721 end add --%>
    </script>
</head>
<body style="padding:0px; overflow:hidden;" onload="toPage()">
<div class="l-loading" style="display:block" id="pageloading"></div>
<form id="form1" action="bill" method="post">
    <div id="toptoolbar"></div>
    <div class="l-panel-search">
        <div class="l-panel-search-item">
            员工：
        </div>
        <div class="l-panel-search-item">
            <select id="number1" name="number">
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
            <input type="text" id="sDate" name="sDate" value="${sDate}"/>
        </div>

        <div class="l-panel-search-item">
            &nbsp;&nbsp;单据名称：
        </div>
        <div class="l-panel-search-item">
            <select id="billName1" name="billName">
                <option value="">全部</option>
                <option value="未处理" <c:if test="${'未处理' eq billName}">selected="selected"</c:if>>未处理</option>
                <c:forEach items="${tBillTypes}" var="tBillType">
                    <option value="${tBillType.billName}" <c:if test="${tBillType.billName eq billName}">selected="selected"</c:if>>${tBillType.billName}</option>
                </c:forEach>
            </select>
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
        <%--yingjing.liu  20160721 start add 添加一个复选框便于做批量调休 --%>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <div class="l-panel-search-item">
            &nbsp;&nbsp;更改为：
        </div>
        <div class="l-panel-search-item">
            <select id="billName3" name="billName3">
                <option value="">请选择单据</option>
                <c:forEach items="${tBillTypes}" var="tBillType">
                    <option value="${tBillType.billName}" <c:if test="${tBillType.billName eq billName}">selected="selected"</c:if>>${tBillType.billName}</option>
                </c:forEach>
            </select>
        </div>
        <div class="l-panel-search-item">
            <input type="button" onclick="changeStatus()" value="更 改"/>
        </div>
        <%--yingjing.liu  20160721 end add 添加一个复选框便于做批量调休 --%>
    </div>
    <div id="maingrid" style="margin:0; padding:0;"></div>

</form>

<div style="display:none;" id="add">
    <form name="form2" method="post" action="bill" id="form2">
        <div>
        </div>
        <table cellpadding="0" cellspacing="0" class="l-table-edit" >
            <tr>
                <td align="right" class="l-table-edit-td">员工:</td>
                <td align="left" class="l-table-edit-td">
                    <select name="pms.number" id="number" ltype="none">
                        <option value="">请选择</option>
                        <c:forEach items="${tUsers}" var="tUser">
                            <option value="${tUser.number}">${tUser.displayName}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td align="right" class="l-table-edit-td">单据名称:</td>
                <td align="left" class="l-table-edit-td">
                    <select name="pms.billName" id="billName" ltype="none">
                        <option value="">请选择</option>
                        <c:forEach items="${tBillTypes}" var="tBillType">
                            <option value="${tBillType.billName}">${tBillType.billName}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td align="right" class="l-table-edit-td">开始时间:</td>
                <td align="left" class="l-table-edit-td"><input name="pms.startDate" type="text" id="startDate" options="{showTime:true,format:'yyyy/MM/dd hh:mm'}" ltype="date" validate="{required:true}"/></td>
                <td align="left"></td>
            </tr>
            <tr>
                <td align="right" class="l-table-edit-td">结束时间:</td>
                <td align="left" class="l-table-edit-td"><input name="pms.endDate" type="text" id="endDate" options="{showTime:true,format:'yyyy/MM/dd hh:mm'}" ltype="date" validate="{required:true}"/></td>
                <td align="left"></td>
            </tr>
            <tr>
                <td align="right" class="l-table-edit-td">描述:</td>

                <td align="left" class="l-table-edit-td"><textarea rows="5" cols="50" name="pms.descs" id="descs" ltype="textarea" validate="{maxlength:300}"></textarea>  </td>
                <td align="left"></td>
            </tr>
        </table>
        <br />
        <input type="hidden" ltype="none" name="op" value="add" />
    </form>
</div>

<div style="display:none;">
</div>

</body>
</html>
