<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>用户</title>


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

    <style type="text/css">
        #maingrid table td a{
            color:#395a7b;
            text-decoration:none;
        }
        #maingrid table td a:hover {
            text-decoration:underline;
        }
    </style>
    <script type="text/javascript">

        //var editID = "";

        /*
        var alert = function (content) {
            $.ligerDialog.alert(content);
        };*/
        var gridManager = null;
        var statusData = [{Status:0,text:'参与考勤'},{Status:1,text:'不参与考勤'},{Status:2,text:'离职'},{Status:3,text:'高管'}];
        $(function () {
            //表格
//            var data = {Rows:[{user_info:'210000010-蔡正龙',day_str:'2014-10-15',start_time:'09:26:54',end_time:'21:05:28',fee1:'null',fee2:'12',fee3:'15',fee4:'null',fee5:'null'}]};
            $("#maingrid").ligerGrid({
                columns: [
                    { display: '用户名', name: 'username', isSort: 'true', width: 150, minWidth: 60 },
                    { display: '姓名', name: 'name',align: 'center', isSort: 'true', width: 150, minWidth: 60 },
                    { display: '工号', name: 'number',align: 'center', isSort: 'true', width: 100, minWidth: 60 },
                    { display: '一级部门', name: 'deptName1',align: 'center', isSort: 'true', width: 100, minWidth: 60 },
                    { display: '二级部门', name: 'deptName2',align: 'center', isSort: 'true', width: 100, minWidth: 60 },
                    { display: '入职日期', name: 'enterDay', align: 'center', isSort: 'true', width: 100, minWidth: 60,
                        editor: {type:'date', format:'yyyy/MM/dd', onChange:function(item) {
                            var before = item.record.enterDay;
                            var after = item.value;
                            if(after == null) { //不能修改为空
                                item.value = before;
                            } else {
                                var date = new Date(after);
                                var afterDay = Admin.formatToDate(date);
                                if(before != afterDay) {
                                    var id = item.record.id;
                                    DWREngine.setAsync(false);
                                    dwr.updateUserEnterDay(id, afterDay, function(data) {
                                        var res = eval("("+data+")");
                                        if(!res.res) {
                                            item.value = before;
                                            Tip.tip(res.msg);
                                        } else {
                                            Tip.tip("修改成功！");
                                            item.value = afterDay;
                                        }
                                    });
                                    DWREngine.setAsync(true);
                                } else { //必须要手动赋值回退，不然就会显示为日期字符串了
                                    item.value = before;
                                }
                            }
                        }},
                        render: function(item) {
                            if(item.enterDay == "") {
                                return "<a href='javascript:void(0);'><!--点击录入--></a>";
                            } else {
                                return "<a href='javascript:void(0);'>"+item.enterDay+"</a>";
                            }
                        }
                    },
                        //yingjing.liu add  20160629 start 添加离职日期
                    { display: '离职日期', name: 'leaveDay', align: 'center', isSort: 'true', width: 100, minWidth: 60,
                         editor: {type:'date', format:'yyyy/MM/dd', onChange:function(item) {
                            var before = item.record.leaveDay;
                            var after = item.value;
                            if(after == null) { //不能修改为空
                                item.value = before;
                            } else {
                                var date = new Date(after);
                                var afterDay = Admin.formatToDate(date);
                                if(before != afterDay) {
                                    var id = item.record.id;
                                    DWREngine.setAsync(false);
                                    dwr.updateUserLeaveDay(id, afterDay, function(data) {
                                        var res = eval("("+data+")");
                                        if(!res.res) {
                                            item.value = before;
                                            Tip.tip(res.msg);
                                        } else {
                                            Tip.tip("修改成功！");
                                            item.value = afterDay;
                                        }
                                    });
                                    DWREngine.setAsync(true);
                                } else { //必须要手动赋值回退，不然就会显示为日期字符串了
                                    item.value = before;
                                }
                            }
                        }},
                        render: function(item) {
                            if(item.leaveDay == "") {
                                return "<a href='javascript:void(0);'></a>";
                            } else {
                                return "<a href='javascript:void(0);'>"+item.leaveDay+"</a>";
                            }
                        }
                    },//yingjing.liu add  20160629 end
                    { display: '状态', name: 'status', align: 'center',isSort: 'true',type:'int', width: 100, minWidth: 60, render: function(item) {
                        if(item.status==null || item.status==0) {
                            return "<a href='javascript:void(0);'>参与考勤</a>";
                        } else if(item.status == 1) {
                            return "<a href='javascript:void(0);'>不参与考勤</a>";
                        } else if(item.status == 2) {
                            return "<a href='javascript:void(0);'>离职</a>"
                        } else if(item.status == 3) {
                            return "<a href='javascript:void(0);'>高管</a>"
                        } else {
                            return item.status;
                        }
                    },editor:{type:'select',data:statusData, valueField:'Status',textField:'text',onChange:function(item) {
                        //alert(item.record.status+":"+item.record.id+":"+item.record.name+":"+item.value);
                        var before = item.record.status;
                        var after = item.value;
                        if(after == '') {
                            item.value = before; //数据回退
                        } else if(before != after) {
                            var id = item.record.id;
                            //if(id == 20042) {item.value = before;}
                            DWREngine.setAsync(false);
                            dwr.updateUserStatus(id, after, function(data) {
                                var res = eval('('+data+')');
                                //alert(data+":"+res.res);
                                if(!res.res) {
                                    item.value = before; //数据回退
                                    $.ligerDialog.alert(res.msg);
                                } else {
                                    Tip.tip("修改成功！");
                                }
                            });
                            DWREngine.setAsync(true);
                        }
                        return true;
                    }} },
                    { display: '操作', name: '', width: 100, align: 'center', render: function (rowdata, rowindex, value)
                    {
                        var h = "";
                        h= "<a href='javascript:modifyItemClick()'>分配部门</a>";
                     //   h += "&nbsp;&nbsp;&nbsp;<a href='javascript:deleteItem()'>删除</a> ";
                        /*  h += "<a href='javascript:deleteRowDataConfirm(" + rowdata.id + ")'>删除</a> ";*/
                        return h;
                    }
                    }

                ], dataAction: 'server', data: ${data}, sortName: 'day_str',enabledEdit:false,
                width: '100%', height: '100%', pageSize: 30, rownumbers: false,
                checkbox: false,
                //应用灰色表头
                cssClass: 'l-grid-gray',
                heightDiff: -6,

                toolbar: {items: [
                   /* { text: '增加', click: btnaddItemClick, icon: 'add',url: ""  },
                    { line: true },
                     { text: '修改', click: modifyItemClick, icon: 'modify' },
                    { line: true },
                    { text: '添加用户', click: addUserForDept, icon: 'add' },*/
                    { line: true }
                ]
                }
            });

            gridManager = $("#maingrid").ligerGetGridManager();

            $("#pageloading").hide();

             $("#number").chosen({
                search_contains:true
            });
            //$("#sDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right'});
            //$("#eDate").ligerDateEditor({labelWidth: 100, labelAlign: 'right'});
        });
        function addUserForDept(){
            alert('添加用户');
            var options = {elementId:'#assign',title:'权限分配'};
            Admin.openDialog(JSON.stringify(options));
        }

        function modifyItemClick( ){
            var row = gridManager.getSelectedRow();
            var id = row.id;
            $("#id").val(id);

            $("#userName1").attr("disabled",false);
            $("#name1").attr("disabled",false);
            $("#userName1").val(row.username);
            $("#name1").val(row.name);
            $("#userName1").attr("disabled",true);
            $("#name1").attr("disabled",true);


            $("#firstDept").find('option[value="'+row.dept1+'"]').attr("selected",true);
            if(row.dept1!=0){// 有一级部门
                $.ajaxSettings.async = false;
                firstChange();
                $("#secondDept").find('option[value="'+row.dept2+'"]').attr("selected",true);
            }else{ //没有一级部门,设为默认不选择
                $("#firstDept").find('option[value=""]').attr("selected",true);
                $("#secondDept").empty();
                $("#secondDept").append($("<option/>").text('请选择').attr("value",''));

            }


            var detailWin = $.ligerDialog.open({ title:"分配部门",target:$("#Editdetail"), height: 500,width: 450,
                buttons: [
                    { text: '保存', onclick: function () { save(); } },
                    { text: '取消', onclick: function () { detailWin.hide();} }
                ]});
        }
        function save(){
           var fid =  $("#firstDept").val();
            var sid = $("#secondDept").val();
            var id = $("#id").val();

            if(fid==''){
                $.ligerDialog.alert('请选择至少一个部门', '提示', 'warn');
                return;
            }
            location.href = "user?op=update&toPage='showDeptUser'&userId="+id+"&firstId="+fid+"&sencondId="+sid;
        }
         function btnaddItemClick(){
            $.ligerDialog.open({ title:"添加用户", url: '${pageContext.request.contextPath}/user?op=add', height: 600,width: 550 });
             
        }


        function syncLdapUser() {
            $.ligerDialog.waitting("正在同步，请稍后...");
            dwr.syncLdapUser(function() {
                $.ligerDialog.closeWaitting();
                location.reload();
            });
        }


        function  firstChange(){
            var first= $("#firstDept").val();
            if(first==''||first=='-1'){
                $("#secondDept").empty();
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
        function addUserToDept(){
            var number = $("#number").val();
            var deptId = "${dept.id}";
            if(number==""||deptId==""){
                $.ligerDialog.warn("添加条件不足");
                return;
            }

            dwr.addUserToDept(deptId,number, function (data) {
                if(data=='添加成功'){
                    window.location.reload();//刷新当前页面
                }else{
                    $.ligerDialog.warn("添加失败");
                }

            });

        }
    </script>
</head>
<body style="padding:0px; overflow:hidden;" onload="toPage()">
<div class="l-loading" style="display:block" id="pageloading"></div>
<form id="form1" action="user" method="post">
    <div id="toptoolbar"></div>
    <div class="l-panel-search">

     <div class="l-panel-search-item">
        员工：
    </div>
    <div class="l-panel-search-item">
        <select id="number" name="number">
            <c:forEach items="${tUsers}" var="tUser">
                <option value="${tUser.number}" <c:if test="${tUser.number eq number}">selected="selected"</c:if>>${tUser.displayName}</option>
            </c:forEach>
        </select>
    </div>
        <div class="l-panel-search-item">
            <input type="button" value="添加" onclick="addUserToDept()">
        </div>
        <%--<div class="l-panel-search-item">
            账户名：
        </div>
        <div class="l-panel-search-item">
            <input type="text" id="username" name="pms.username" value="${pms.username}"/>
        </div>
        <div class="l-panel-search-item">
            部门：
        </div>
        <div class="l-panel-search-item">
            <input type="text" id="depart" name="pms.depart" value="${pms.depart}"/>
        </div>
        <div class="l-panel-search-item">
            <input type="submit" value="搜 索"/>
        </div>
        <div style="float:right;margin-right:10px;">
            <input type="button" value="Sync WTK User" onclick="syncLdapUser();">
        </div>--%>
    </div>

    <div id="maingrid" style="margin:0; padding:0"></div>

</form>
<div style="display:none;">

</div>

<%--yingjing.liu 20160725 start  分配部门窗口--%>


<div style="display:none" id="assign">
    <form name="form2" method="post" action="permission" id="form2">
        <table class="data-form" cellspacing="0" cellpadding="0">
            <tbody>
            <tr>
                <th scope="row">权限名称：</th>
                <td id="perName">
                </td>
            </tr>
            <tr>
                <th scope="row">用户：</th>
                <td>
                    <ol class="fcbk">
                        <li id="facebook-list1" class="input-text facebook-list">
                            <label></label>
                            <input type="text" value="无用户" id="user" name="userIds" class="input-normal" style="width:315px;" disabled="disabled"/>
                            <ul id="preadded1" style="display:none">
                                <c:forEach items="${userPermissions }" var="userPermission">
                                    <li rel="${userPermission.user.id }">${userPermission.user.name }</li>
                                </c:forEach>
                            </ul>
                            <div id="facebook-auto1" class="facebook-auto">
                                <div id="default1" class="default">Type the name of an User writer you like</div>
                                <ul id="feed1">
                                </ul>
                            </div>
                        </li>
                    </ol>
                </td>
            </tr>
            <tr>
                <th scope="row">部门：</th>
                <td>
                    <ol class="fcbk">
                        <li id="facebook-list2" class="input-text facebook-list">
                            <label></label>
                            <input type="text" value="没有部门" id="department" name="departmentIds" class="input-normal" style="width:315px;" disabled="disabled"/>
                            <ul id="preadded2" style="display:none">
                                <c:forEach items="${departmentPermissions }" var="departmentPermission">
                                    <li rel="${departmentPermission.department.id }">${departmentPermission.department.departmentName }</li>
                                </c:forEach>
                            </ul>
                            <div id="facebook-auto2" class="facebook-auto">
                                <div id="default2" class="default">Type the name of an Department writer you like</div>
                                <ul id="feed2">
                                </ul>
                            </div>
                        </li>
                    </ol>
                </td>
            </tr>
            <tr>
                <th>&nbsp;</th>
                <td><input type="hidden" name="op" value="assign">
                    <input type="hidden" id="permissionId" name="permissionId" value="0">
                </td>
            </tr>
            </tbody>
        </table>
    </form>
</div>



<div id="Editdetail" style="display:none;">
    <form id="EditUserForm"  action="dept" method="post">
        <input type="hidden" name="op" id="op" >
        <input type="hidden" name="id" id="id">
        <table style="height: 350px">
            <tr>
                <td style="align:right">用户名称：</td>
                <td><input type="text" name="userName1" id="userName1"></td>
            </tr>
            <tr>
                <td style="align:right">姓名：</td>
                <td>
                    <input type="text" id="name1">
                </td>
            </tr>

            <tr  id="div_type1" >
                <td style="align:right">选择一级部门:</td>
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
                <td style="align:right">二级部门：</td>
                <td>
                    <select id="secondDept" name="secondDept">
                        <option value="">请选择</option>
                    </select>
                </td>
            </tr>
        </table>
    </form>
</div>
<%--yingjing.liu 20160725 end --%>
</body>
</html>
